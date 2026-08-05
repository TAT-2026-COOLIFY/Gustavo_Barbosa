package com.ironfit.api.ia.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.dieta.dto.DietaRequestDTO;
import com.ironfit.api.dieta.dto.DietaResponseDTO;
import com.ironfit.api.dieta.entity.OrigemDieta;
import com.ironfit.api.dieta.repository.AlimentoRepository;
import com.ironfit.api.dieta.service.DietaService;
import com.ironfit.api.exception.BusinessException;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.ia.client.MensagemChat;
import com.ironfit.api.ia.client.OpenAiChatClient;
import com.ironfit.api.ia.dto.ConversaDetalheDTO;
import com.ironfit.api.ia.dto.ConversaResumoDTO;
import com.ironfit.api.ia.dto.EnviarMensagemRequestDTO;
import com.ironfit.api.ia.dto.EnviarMensagemResponseDTO;
import com.ironfit.api.ia.dto.MensagemComRascunhoDTO;
import com.ironfit.api.ia.dto.MensagemResponseDTO;
import com.ironfit.api.ia.dto.RascunhoResponseDTO;
import com.ironfit.api.ia.dto.RespostaBrutaIaDTO;
import com.ironfit.api.ia.dto.SugestaoBrutaDTO;
import com.ironfit.api.ia.entity.ConversaIa;
import com.ironfit.api.ia.entity.MensagemIa;
import com.ironfit.api.ia.entity.PapelMensagemIa;
import com.ironfit.api.ia.entity.RascunhoIa;
import com.ironfit.api.ia.entity.StatusRascunhoIa;
import com.ironfit.api.ia.entity.TipoRascunhoIa;
import com.ironfit.api.ia.repository.ConversaIaRepository;
import com.ironfit.api.ia.repository.MensagemIaRepository;
import com.ironfit.api.ia.repository.RascunhoIaRepository;
import com.ironfit.api.treino.dto.TreinoRequestDTO;
import com.ironfit.api.treino.dto.TreinoResponseDTO;
import com.ironfit.api.treino.entity.OrigemTreino;
import com.ironfit.api.treino.repository.ExercicioRepository;
import com.ironfit.api.treino.service.TreinoService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Assistente conversacional: mantém o histórico de conversas, monta o prompt (com o catálogo real de
 * exercícios/alimentos para o modelo só sugerir ids válidos), chama o provedor de IA e, quando a resposta
 * pedir um treino/dieta, guarda um {@link RascunhoIa} pendente de revisão do usuário. Confirmar o rascunho
 * delega para {@link TreinoService}/{@link DietaService} com origem IA — nenhuma regra de negócio é duplicada.
 */
@Service
public class IaChatService {

    private static final Logger log = LoggerFactory.getLogger(IaChatService.class);
    private static final int MAX_ITENS_CATALOGO = 200;

    private final ConversaIaRepository conversaIaRepository;
    private final MensagemIaRepository mensagemIaRepository;
    private final RascunhoIaRepository rascunhoIaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ExercicioRepository exercicioRepository;
    private final AlimentoRepository alimentoRepository;
    private final TreinoService treinoService;
    private final DietaService dietaService;
    private final OpenAiChatClient openAiChatClient;
    private final ObjectMapper objectMapper;

    public IaChatService(
            ConversaIaRepository conversaIaRepository,
            MensagemIaRepository mensagemIaRepository,
            RascunhoIaRepository rascunhoIaRepository,
            UsuarioRepository usuarioRepository,
            ExercicioRepository exercicioRepository,
            AlimentoRepository alimentoRepository,
            TreinoService treinoService,
            DietaService dietaService,
            OpenAiChatClient openAiChatClient,
            ObjectMapper objectMapper) {
        this.conversaIaRepository = conversaIaRepository;
        this.mensagemIaRepository = mensagemIaRepository;
        this.rascunhoIaRepository = rascunhoIaRepository;
        this.usuarioRepository = usuarioRepository;
        this.exercicioRepository = exercicioRepository;
        this.alimentoRepository = alimentoRepository;
        this.treinoService = treinoService;
        this.dietaService = dietaService;
        this.openAiChatClient = openAiChatClient;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public EnviarMensagemResponseDTO enviarMensagem(Long usuarioId, EnviarMensagemRequestDTO request) {
        ConversaIa conversa = obterOuCriarConversa(usuarioId, request.conversaId(), request.mensagem());
        MensagemIa mensagemUsuario = salvarMensagem(conversa, PapelMensagemIa.USUARIO, request.mensagem());

        List<MensagemChat> historico = montarHistorico(usuarioId, conversa);
        String conteudoBruto = openAiChatClient.enviarConversa(historico);
        RespostaBrutaIaDTO respostaBruta = interpretarResposta(conteudoBruto);

        MensagemIa mensagemAssistente = salvarMensagem(conversa, PapelMensagemIa.ASSISTENTE, respostaBruta.resposta());
        tocarConversa(conversa);

        RascunhoResponseDTO rascunhoDTO = null;
        if (respostaBruta.sugestao() != null) {
            RascunhoCriado criado = criarRascunhoSeValido(conversa, mensagemAssistente, respostaBruta.sugestao());
            if (criado != null) {
                rascunhoDTO = new RascunhoResponseDTO(
                        criado.entidade().getId(), criado.entidade().getTipo(), criado.entidade().getStatus(), criado.dados());
            }
        }

        return new EnviarMensagemResponseDTO(
                conversa.getId(), toMensagemDTO(mensagemUsuario), toMensagemDTO(mensagemAssistente), rascunhoDTO);
    }

    @Transactional(readOnly = true)
    public List<ConversaResumoDTO> listarConversas(Long usuarioId) {
        return conversaIaRepository.findByUsuarioIdOrderByAtualizadoEmDesc(usuarioId).stream()
                .map(c -> new ConversaResumoDTO(c.getId(), c.getTitulo(), c.getCriadoEm(), c.getAtualizadoEm()))
                .toList();
    }

    @Transactional(readOnly = true)
    public ConversaDetalheDTO obterConversa(Long usuarioId, Long conversaId) {
        ConversaIa conversa = conversaIaRepository
                .findByIdAndUsuarioId(conversaId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversa não encontrada"));

        List<MensagemIa> mensagens = mensagemIaRepository.findByConversaIdOrderByCriadoEmAsc(conversaId);
        List<Long> mensagemIds = mensagens.stream().map(MensagemIa::getId).toList();
        Map<Long, RascunhoIa> rascunhosPorMensagem = rascunhoIaRepository.findByMensagemIdIn(mensagemIds).stream()
                .collect(Collectors.toMap(r -> r.getMensagem().getId(), r -> r));

        List<MensagemComRascunhoDTO> mensagensDTO = mensagens.stream()
                .map(m -> new MensagemComRascunhoDTO(
                        m.getId(),
                        m.getPapel(),
                        m.getConteudo(),
                        m.getCriadoEm(),
                        toRascunhoDTO(rascunhosPorMensagem.get(m.getId()))))
                .toList();

        return new ConversaDetalheDTO(conversa.getId(), conversa.getTitulo(), mensagensDTO);
    }

    @Transactional
    public TreinoResponseDTO confirmarTreino(Long usuarioId, Long rascunhoId, TreinoRequestDTO request) {
        RascunhoIa rascunho = buscarRascunhoPendente(usuarioId, rascunhoId, TipoRascunhoIa.TREINO);
        TreinoResponseDTO criado = treinoService.criar(usuarioId, request, OrigemTreino.IA);
        rascunho.setStatus(StatusRascunhoIa.CONFIRMADO);
        rascunhoIaRepository.save(rascunho);
        return criado;
    }

    @Transactional
    public DietaResponseDTO confirmarDieta(Long usuarioId, Long rascunhoId, DietaRequestDTO request) {
        RascunhoIa rascunho = buscarRascunhoPendente(usuarioId, rascunhoId, TipoRascunhoIa.DIETA);
        DietaResponseDTO criada = dietaService.criar(usuarioId, request, OrigemDieta.IA);
        rascunho.setStatus(StatusRascunhoIa.CONFIRMADO);
        rascunhoIaRepository.save(rascunho);
        return criada;
    }

    @Transactional
    public void descartarRascunho(Long usuarioId, Long rascunhoId) {
        RascunhoIa rascunho = rascunhoIaRepository
                .findByIdAndUsuarioId(rascunhoId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Rascunho não encontrado"));
        garantirPendente(rascunho);
        rascunho.setStatus(StatusRascunhoIa.DESCARTADO);
        rascunhoIaRepository.save(rascunho);
    }

    private ConversaIa obterOuCriarConversa(Long usuarioId, Long conversaId, String primeiraMensagem) {
        if (conversaId != null) {
            return conversaIaRepository
                    .findByIdAndUsuarioId(conversaId, usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Conversa não encontrada"));
        }
        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        ConversaIa conversa =
                ConversaIa.builder().usuario(usuario).titulo(truncar(primeiraMensagem, 60)).build();
        return conversaIaRepository.save(conversa);
    }

    private MensagemIa salvarMensagem(ConversaIa conversa, PapelMensagemIa papel, String conteudo) {
        MensagemIa mensagem = MensagemIa.builder().conversa(conversa).papel(papel).conteudo(conteudo).build();
        return mensagemIaRepository.save(mensagem);
    }

    private void tocarConversa(ConversaIa conversa) {
        conversa.setAtualizadoEm(Instant.now());
        conversaIaRepository.save(conversa);
    }

    private List<MensagemChat> montarHistorico(Long usuarioId, ConversaIa conversa) {
        List<MensagemChat> mensagens = new ArrayList<>();
        mensagens.add(MensagemChat.sistema(construirPromptSistema(usuarioId)));
        for (MensagemIa mensagem : mensagemIaRepository.findByConversaIdOrderByCriadoEmAsc(conversa.getId())) {
            mensagens.add(
                    mensagem.getPapel() == PapelMensagemIa.USUARIO
                            ? MensagemChat.usuario(mensagem.getConteudo())
                            : MensagemChat.assistente(mensagem.getConteudo()));
        }
        return mensagens;
    }

    private RespostaBrutaIaDTO interpretarResposta(String conteudoBruto) {
        try {
            RespostaBrutaIaDTO resposta = objectMapper.readValue(conteudoBruto, RespostaBrutaIaDTO.class);
            if (resposta.resposta() == null || resposta.resposta().isBlank()) {
                return new RespostaBrutaIaDTO(conteudoBruto, resposta.sugestao());
            }
            return resposta;
        } catch (JsonProcessingException e) {
            log.warn("Resposta da IA fora do formato JSON esperado, tratando como texto simples: {}", e.getMessage());
            return new RespostaBrutaIaDTO(conteudoBruto, null);
        }
    }

    private RascunhoCriado criarRascunhoSeValido(ConversaIa conversa, MensagemIa mensagemAssistente, SugestaoBrutaDTO sugestao) {
        try {
            Object dados =
                    switch (sugestao.tipo()) {
                        case TREINO -> objectMapper.treeToValue(sugestao.dados(), TreinoRequestDTO.class);
                        case DIETA -> objectMapper.treeToValue(sugestao.dados(), DietaRequestDTO.class);
                    };

            RascunhoIa rascunho = RascunhoIa.builder()
                    .usuario(conversa.getUsuario())
                    .conversa(conversa)
                    .mensagem(mensagemAssistente)
                    .tipo(sugestao.tipo())
                    .conteudoJson(objectMapper.writeValueAsString(dados))
                    .status(StatusRascunhoIa.PENDENTE)
                    .build();
            rascunhoIaRepository.save(rascunho);
            return new RascunhoCriado(rascunho, dados);
        } catch (Exception e) {
            log.warn(
                    "Sugestão retornada pela IA não pôde ser interpretada como {}, ignorando rascunho: {}",
                    sugestao.tipo(),
                    e.getMessage());
            return null;
        }
    }

    private RascunhoResponseDTO toRascunhoDTO(RascunhoIa rascunho) {
        if (rascunho == null) {
            return null;
        }
        try {
            Object dados =
                    rascunho.getTipo() == TipoRascunhoIa.TREINO
                            ? objectMapper.readValue(rascunho.getConteudoJson(), TreinoRequestDTO.class)
                            : objectMapper.readValue(rascunho.getConteudoJson(), DietaRequestDTO.class);
            return new RascunhoResponseDTO(rascunho.getId(), rascunho.getTipo(), rascunho.getStatus(), dados);
        } catch (JsonProcessingException e) {
            log.error("Não foi possível reidratar o rascunho {}", rascunho.getId(), e);
            return new RascunhoResponseDTO(rascunho.getId(), rascunho.getTipo(), rascunho.getStatus(), null);
        }
    }

    private RascunhoIa buscarRascunhoPendente(Long usuarioId, Long rascunhoId, TipoRascunhoIa tipoEsperado) {
        RascunhoIa rascunho = rascunhoIaRepository
                .findByIdAndUsuarioId(rascunhoId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Rascunho não encontrado"));
        if (rascunho.getTipo() != tipoEsperado) {
            throw new BusinessException("Rascunho não é do tipo " + tipoEsperado);
        }
        garantirPendente(rascunho);
        return rascunho;
    }

    private void garantirPendente(RascunhoIa rascunho) {
        if (rascunho.getStatus() != StatusRascunhoIa.PENDENTE) {
            String situacao = rascunho.getStatus() == StatusRascunhoIa.CONFIRMADO ? "confirmado" : "descartado";
            throw new BusinessException("Este rascunho já foi " + situacao);
        }
    }

    private MensagemResponseDTO toMensagemDTO(MensagemIa mensagem) {
        return new MensagemResponseDTO(mensagem.getId(), mensagem.getPapel(), mensagem.getConteudo(), mensagem.getCriadoEm());
    }

    private String truncar(String texto, int max) {
        String limpo = texto.strip();
        return limpo.length() <= max ? limpo : limpo.substring(0, max - 1).stripTrailing() + "…";
    }

    private String construirPromptSistema(Long usuarioId) {
        StringBuilder prompt = new StringBuilder(
                """
                Você é o assistente de IA do IronFit, um app de treinos, dieta e bem-estar. Converse em português \
                do Brasil, tom motivador e direto. Seu objetivo é tirar dúvidas de treino/dieta e, quando o \
                usuário pedir, montar um treino ou uma dieta completos usando SOMENTE itens do catálogo abaixo.

                Antes de sugerir um treino ou dieta, se faltar alguma informação essencial (objetivo, nível de \
                experiência ou atividade física, peso corporal para dieta), pergunte antes de gerar qualquer sugestão.

                Responda SEMPRE com um único objeto JSON, sem nenhum texto fora dele, no formato:
                {"resposta": "texto em português para mostrar ao usuário no chat", "sugestao": null}

                Ou, quando for gerar um treino completo:
                {"resposta": "texto explicando a sugestão", "sugestao": {"tipo": "TREINO", "dados": {
                  "nome": "string", "exercicios": [
                    {"exercicioId": 1, "ordem": 1, "seriesAlvo": 3, "repeticoesAlvo": 12, "cargaAlvoKg": null, "descansoSegundos": 60}
                  ]}}}

                Ou, quando for gerar uma dieta completa:
                {"resposta": "texto explicando a sugestão", "sugestao": {"tipo": "DIETA", "dados": {
                  "nome": "string", "calculoAutomatico": true, "pesoReferenciaKg": 80, "nivelAtividade": "MODERADO",
                  "tipoObjetivoReferencia": "HIPERTROFIA", "metaCaloriasKcal": null, "metaProteinaG": null,
                  "metaCarboidratoG": null, "metaGorduraG": null, "metaAguaMl": null, "refeicoes": [
                    {"nome": "Café da manhã", "ordem": 1, "horarioSugerido": "07:00", "alimentos": [
                      {"alimentoId": 3, "ordem": 1, "quantidadeGramas": 100}
                    ]}
                  ]}}}

                Regras importantes:
                - "exercicioId" e "alimentoId" DEVEM ser ids reais das listas de catálogo abaixo. Nunca invente ids.
                - Em dieta, prefira sempre calculoAutomatico=true e deixe os campos metaCaloriasKcal, metaProteinaG, \
                metaCarboidratoG, metaGorduraG e metaAguaMl como null — o próprio sistema calcula as metas \
                nutricionais a partir de pesoReferenciaKg, nivelAtividade e tipoObjetivoReferencia.
                - nivelAtividade deve ser um destes: SEDENTARIO, LEVE, MODERADO, INTENSO, MUITO_INTENSO.
                - tipoObjetivoReferencia deve ser um destes: EMAGRECIMENTO, HIPERTROFIA, MANUTENCAO, CONDICIONAMENTO, SAUDE.
                - Nunca gere "sugestao" para assuntos que não sejam pedidos explícitos de treino ou dieta.
                """);

        prompt.append("\nCatálogo de exercícios (id: nome — grupo muscular):\n");
        exercicioRepository.findAllByOrderByNome().stream()
                .limit(MAX_ITENS_CATALOGO)
                .forEach(ex -> prompt.append(ex.getId())
                        .append(": ")
                        .append(ex.getNome())
                        .append(" — ")
                        .append(ex.getGrupoMuscular())
                        .append('\n'));

        prompt.append("\nCatálogo de alimentos (id: nome — categoria — kcal/proteína/carboidrato/gordura por 100g):\n");
        alimentoRepository.buscar(usuarioId, null, null).stream()
                .limit(MAX_ITENS_CATALOGO)
                .forEach(al -> prompt.append(al.getId())
                        .append(": ")
                        .append(al.getNome())
                        .append(" — ")
                        .append(al.getCategoria())
                        .append(" — ")
                        .append(al.getCaloriasPor100g())
                        .append("kcal/")
                        .append(al.getProteinaPor100g())
                        .append("g/")
                        .append(al.getCarboidratoPor100g())
                        .append("g/")
                        .append(al.getGorduraPor100g())
                        .append("g\n"));

        return prompt.toString();
    }

    private record RascunhoCriado(RascunhoIa entidade, Object dados) {}
}
