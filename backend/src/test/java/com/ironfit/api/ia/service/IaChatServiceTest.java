package com.ironfit.api.ia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.dieta.dto.DietaRequestDTO;
import com.ironfit.api.dieta.dto.DietaResponseDTO;
import com.ironfit.api.dieta.entity.OrigemDieta;
import com.ironfit.api.dieta.repository.AlimentoRepository;
import com.ironfit.api.dieta.service.DietaService;
import com.ironfit.api.exception.BusinessException;
import com.ironfit.api.ia.client.OpenAiChatClient;
import com.ironfit.api.ia.dto.EnviarMensagemRequestDTO;
import com.ironfit.api.ia.dto.EnviarMensagemResponseDTO;
import com.ironfit.api.ia.entity.ConversaIa;
import com.ironfit.api.ia.entity.MensagemIa;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IaChatServiceTest {

    private static final Long USUARIO_ID = 1L;

    @Mock
    private ConversaIaRepository conversaIaRepository;

    @Mock
    private MensagemIaRepository mensagemIaRepository;

    @Mock
    private RascunhoIaRepository rascunhoIaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ExercicioRepository exercicioRepository;

    @Mock
    private AlimentoRepository alimentoRepository;

    @Mock
    private TreinoService treinoService;

    @Mock
    private DietaService dietaService;

    @Mock
    private OpenAiChatClient openAiChatClient;

    private IaChatService iaChatService;
    private AtomicLong proximoId;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        iaChatService = new IaChatService(
                conversaIaRepository,
                mensagemIaRepository,
                rascunhoIaRepository,
                usuarioRepository,
                exercicioRepository,
                alimentoRepository,
                treinoService,
                dietaService,
                openAiChatClient,
                objectMapper);

        proximoId = new AtomicLong(1);
        lenient().when(conversaIaRepository.save(any(ConversaIa.class))).thenAnswer(inv -> {
            ConversaIa c = inv.getArgument(0);
            if (c.getId() == null) c.setId(proximoId.getAndIncrement());
            return c;
        });
        lenient().when(mensagemIaRepository.save(any(MensagemIa.class))).thenAnswer(inv -> {
            MensagemIa m = inv.getArgument(0);
            if (m.getId() == null) m.setId(proximoId.getAndIncrement());
            return m;
        });
        lenient().when(rascunhoIaRepository.save(any(RascunhoIa.class))).thenAnswer(inv -> {
            RascunhoIa r = inv.getArgument(0);
            if (r.getId() == null) r.setId(proximoId.getAndIncrement());
            return r;
        });
    }

    private void stubUsuario() {
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(Usuario.builder().id(USUARIO_ID).build()));
    }

    @Test
    void enviarMensagem_semSugestao_deveCriarConversaESalvarAsDuasMensagens() {
        stubUsuario();
        when(openAiChatClient.enviarConversa(anyList()))
                .thenReturn("{\"resposta\": \"Claro, posso ajudar!\", \"sugestao\": null}");

        EnviarMensagemResponseDTO resultado =
                iaChatService.enviarMensagem(USUARIO_ID, new EnviarMensagemRequestDTO(null, "Oi, tudo bem?"));

        assertThat(resultado.conversaId()).isNotNull();
        assertThat(resultado.mensagemUsuario().conteudo()).isEqualTo("Oi, tudo bem?");
        assertThat(resultado.mensagemAssistente().conteudo()).isEqualTo("Claro, posso ajudar!");
        assertThat(resultado.rascunho()).isNull();
        verify(conversaIaRepository, atLeastOnce()).save(any(ConversaIa.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void enviarMensagem_promptDeSistemaDeveRestringirEscopoAoIronFit() {
        stubUsuario();
        when(openAiChatClient.enviarConversa(anyList()))
                .thenReturn("{\"resposta\": \"" + IaChatService.MENSAGEM_FORA_DO_ESCOPO + "\", \"sugestao\": null}");

        EnviarMensagemResponseDTO resultado = iaChatService.enviarMensagem(
                USUARIO_ID, new EnviarMensagemRequestDTO(null, "Quem ganhou a Copa de 2002?"));

        var captor = org.mockito.ArgumentCaptor.forClass(java.util.List.class);
        verify(openAiChatClient).enviarConversa(captor.capture());
        java.util.List<com.ironfit.api.ia.client.MensagemChat> enviadas = captor.getValue();
        String promptSistema = enviadas.get(0).content();
        assertThat(enviadas.get(0).role()).isEqualTo("system");
        assertThat(promptSistema).contains("ESCOPO").contains(IaChatService.MENSAGEM_FORA_DO_ESCOPO);
        assertThat(promptSistema).doesNotContain("%s");
        assertThat(resultado.mensagemAssistente().conteudo()).isEqualTo(IaChatService.MENSAGEM_FORA_DO_ESCOPO);
        assertThat(resultado.rascunho()).isNull();
    }

    @Test
    void enviarMensagem_modeloMarcaForaDoEscopoMasResponde_backendDeveSubstituirPelaMensagemPadrao() {
        stubUsuario();
        when(openAiChatClient.enviarConversa(anyList()))
                .thenReturn("{\"resposta\": \"2x2 é 4\", \"sugestao\": null, \"foraDoEscopo\": true}");

        EnviarMensagemResponseDTO resultado =
                iaChatService.enviarMensagem(USUARIO_ID, new EnviarMensagemRequestDTO(null, "quanto é 2x2"));

        assertThat(resultado.mensagemAssistente().conteudo()).isEqualTo(IaChatService.MENSAGEM_FORA_DO_ESCOPO);
        assertThat(resultado.rascunho()).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void enviarMensagem_historicoNaoDeveReenviarPerguntasRecusadasEDeveTerminarComLembreteDeEscopo() {
        ConversaIa conversa = ConversaIa.builder().id(50L).usuario(Usuario.builder().id(USUARIO_ID).build()).build();
        when(conversaIaRepository.findByIdAndUsuarioId(50L, USUARIO_ID)).thenReturn(Optional.of(conversa));
        var usuario = com.ironfit.api.ia.entity.PapelMensagemIa.USUARIO;
        var assistente = com.ironfit.api.ia.entity.PapelMensagemIa.ASSISTENTE;
        when(mensagemIaRepository.findByConversaIdOrderByCriadoEmAsc(50L))
                .thenReturn(List.of(
                        MensagemIa.builder().papel(usuario).conteudo("quantos mortos na 2 guerra").build(),
                        MensagemIa.builder().papel(assistente).conteudo(IaChatService.MENSAGEM_FORA_DO_ESCOPO).build(),
                        MensagemIa.builder().papel(usuario).conteudo("quanto de proteína devo comer?").build()));
        when(openAiChatClient.enviarConversa(anyList()))
                .thenReturn("{\"resposta\": \"Cerca de 2g/kg.\", \"sugestao\": null, \"foraDoEscopo\": false}");

        iaChatService.enviarMensagem(USUARIO_ID, new EnviarMensagemRequestDTO(50L, "quanto de proteína devo comer?"));

        var captor = org.mockito.ArgumentCaptor.forClass(List.class);
        verify(openAiChatClient).enviarConversa(captor.capture());
        List<com.ironfit.api.ia.client.MensagemChat> enviadas = captor.getValue();
        assertThat(enviadas).extracting(com.ironfit.api.ia.client.MensagemChat::content)
                .doesNotContain("quantos mortos na 2 guerra", IaChatService.MENSAGEM_FORA_DO_ESCOPO)
                .contains("quanto de proteína devo comer?");
        assertThat(enviadas.get(enviadas.size() - 1).role()).isEqualTo("system");
        assertThat(enviadas.get(enviadas.size() - 1).content()).contains("ESCOPO");
    }

    @Test
    void pedeGeracao_detectaPedidoDeTreinoOuDietaNasUltimasMensagensDoUsuario() {
        var usuario = com.ironfit.api.ia.entity.PapelMensagemIa.USUARIO;
        var assistente = com.ironfit.api.ia.entity.PapelMensagemIa.ASSISTENTE;
        var pergunta = MensagemIa.builder().papel(usuario).conteudo("quanta água devo beber?").build();
        var pedido = MensagemIa.builder().papel(usuario).conteudo("Monte uma DIETA pra mim").build();
        var perguntaPeso = MensagemIa.builder().papel(assistente).conteudo("Qual o seu peso?").build();
        var respostaPeso = MensagemIa.builder().papel(usuario).conteudo("80kg").build();
        var cardapio = MensagemIa.builder().papel(usuario).conteudo("faz um cardápio").build();

        assertThat(IaChatService.pedeGeracao(List.of(pergunta))).isFalse();
        assertThat(IaChatService.pedeGeracao(List.of(pedido, perguntaPeso, respostaPeso))).isTrue();
        assertThat(IaChatService.pedeGeracao(List.of(cardapio))).isTrue();
    }

    @Test
    @SuppressWarnings("unchecked")
    void enviarMensagem_perguntaSimples_naoDeveEnviarCatalogo() {
        stubUsuario();
        when(openAiChatClient.enviarConversa(anyList()))
                .thenReturn("{\"resposta\": \"Cerca de 35ml/kg.\", \"sugestao\": null, \"foraDoEscopo\": false}");

        iaChatService.enviarMensagem(USUARIO_ID, new EnviarMensagemRequestDTO(null, "quanta água devo beber?"));

        var captor = org.mockito.ArgumentCaptor.forClass(List.class);
        verify(openAiChatClient).enviarConversa(captor.capture());
        List<com.ironfit.api.ia.client.MensagemChat> enviadas = captor.getValue();
        assertThat(enviadas.get(0).content()).doesNotContain("Catálogo de exercícios").contains("ESCOPO");
        verify(exercicioRepository, never()).findAllByOrderByNome();
    }

    @Test
    void enviarMensagem_comSugestaoDeTreinoValida_deveCriarRascunhoPendente() {
        stubUsuario();
        String jsonResposta =
                """
                {"resposta": "Aqui está seu treino!", "sugestao": {"tipo": "TREINO", "dados": {
                  "nome": "Treino A", "exercicios": [
                    {"exercicioId": 1, "ordem": 1, "seriesAlvo": 3, "repeticoesAlvo": 12, "cargaAlvoKg": null, "descansoSegundos": 60}
                  ]}}}
                """;
        when(openAiChatClient.enviarConversa(anyList())).thenReturn(jsonResposta);

        EnviarMensagemResponseDTO resultado =
                iaChatService.enviarMensagem(USUARIO_ID, new EnviarMensagemRequestDTO(null, "Monta um treino de peito pra mim"));

        assertThat(resultado.rascunho()).isNotNull();
        assertThat(resultado.rascunho().tipo()).isEqualTo(TipoRascunhoIa.TREINO);
        assertThat(resultado.rascunho().status()).isEqualTo(StatusRascunhoIa.PENDENTE);
        assertThat(resultado.rascunho().dados()).isInstanceOf(TreinoRequestDTO.class);
        verify(rascunhoIaRepository).save(any(RascunhoIa.class));
    }

    @Test
    void enviarMensagem_comSugestaoMalFormada_deveIgnorarRascunhoMasManterTextoDaResposta() {
        stubUsuario();
        String jsonResposta =
                "{\"resposta\": \"tentando gerar...\", \"sugestao\": {\"tipo\": \"TREINO\", \"dados\": {\"nome\": \"x\", \"exercicios\": \"não é uma lista\"}}}";
        when(openAiChatClient.enviarConversa(anyList())).thenReturn(jsonResposta);

        EnviarMensagemResponseDTO resultado =
                iaChatService.enviarMensagem(USUARIO_ID, new EnviarMensagemRequestDTO(null, "Monta um treino"));

        assertThat(resultado.mensagemAssistente().conteudo()).isEqualTo("tentando gerar...");
        assertThat(resultado.rascunho()).isNull();
        verify(rascunhoIaRepository, never()).save(any());
    }

    @Test
    void enviarMensagem_respostaForaDoFormatoJson_deveTratarComoTextoSimples() {
        stubUsuario();
        when(openAiChatClient.enviarConversa(anyList())).thenReturn("desculpe, não entendi (isso não é JSON)");

        EnviarMensagemResponseDTO resultado =
                iaChatService.enviarMensagem(USUARIO_ID, new EnviarMensagemRequestDTO(null, "oi"));

        assertThat(resultado.mensagemAssistente().conteudo()).isEqualTo("desculpe, não entendi (isso não é JSON)");
        assertThat(resultado.rascunho()).isNull();
    }

    @Test
    void confirmarTreino_rascunhoPendente_deveCriarTreinoComOrigemIaEMarcarConfirmado() {
        RascunhoIa rascunho = RascunhoIa.builder()
                .id(9L)
                .tipo(TipoRascunhoIa.TREINO)
                .status(StatusRascunhoIa.PENDENTE)
                .conteudoJson("{}")
                .build();
        when(rascunhoIaRepository.findByIdAndUsuarioId(9L, USUARIO_ID)).thenReturn(Optional.of(rascunho));

        TreinoRequestDTO request = new TreinoRequestDTO("Treino A", List.of());
        TreinoResponseDTO respostaEsperada = new TreinoResponseDTO(5L, "Treino A", OrigemTreino.IA, false, List.of());
        when(treinoService.criar(USUARIO_ID, request, OrigemTreino.IA)).thenReturn(respostaEsperada);

        TreinoResponseDTO resultado = iaChatService.confirmarTreino(USUARIO_ID, 9L, request);

        assertThat(resultado).isEqualTo(respostaEsperada);
        assertThat(rascunho.getStatus()).isEqualTo(StatusRascunhoIa.CONFIRMADO);
        verify(treinoService).criar(USUARIO_ID, request, OrigemTreino.IA);
    }

    @Test
    void confirmarTreino_rascunhoJaConfirmado_deveLancarBusinessException() {
        RascunhoIa rascunho = RascunhoIa.builder()
                .id(9L)
                .tipo(TipoRascunhoIa.TREINO)
                .status(StatusRascunhoIa.CONFIRMADO)
                .conteudoJson("{}")
                .build();
        when(rascunhoIaRepository.findByIdAndUsuarioId(9L, USUARIO_ID)).thenReturn(Optional.of(rascunho));

        assertThatThrownBy(() -> iaChatService.confirmarTreino(USUARIO_ID, 9L, new TreinoRequestDTO("Treino A", List.of())))
                .isInstanceOf(BusinessException.class);
        verify(treinoService, never()).criar(any(), any(), any());
    }

    @Test
    void confirmarTreino_rascunhoDeOutroTipo_deveLancarBusinessException() {
        RascunhoIa rascunho = RascunhoIa.builder()
                .id(9L)
                .tipo(TipoRascunhoIa.DIETA)
                .status(StatusRascunhoIa.PENDENTE)
                .conteudoJson("{}")
                .build();
        when(rascunhoIaRepository.findByIdAndUsuarioId(9L, USUARIO_ID)).thenReturn(Optional.of(rascunho));

        assertThatThrownBy(() -> iaChatService.confirmarTreino(USUARIO_ID, 9L, new TreinoRequestDTO("Treino A", List.of())))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void confirmarDieta_rascunhoPendente_deveCriarDietaComOrigemIaEMarcarConfirmado() {
        RascunhoIa rascunho = RascunhoIa.builder()
                .id(3L)
                .tipo(TipoRascunhoIa.DIETA)
                .status(StatusRascunhoIa.PENDENTE)
                .conteudoJson("{}")
                .build();
        when(rascunhoIaRepository.findByIdAndUsuarioId(3L, USUARIO_ID)).thenReturn(Optional.of(rascunho));

        DietaRequestDTO request =
                new DietaRequestDTO("Dieta A", null, null, null, false, 2000, 150, 200, 60, 2000, List.of());
        DietaResponseDTO respostaEsperada = new DietaResponseDTO(
                7L, "Dieta A", OrigemDieta.IA, false, false, null, null, null, false, 2000, 150, 200, 60, 2000, List.of());
        when(dietaService.criar(USUARIO_ID, request, OrigemDieta.IA)).thenReturn(respostaEsperada);

        DietaResponseDTO resultado = iaChatService.confirmarDieta(USUARIO_ID, 3L, request);

        assertThat(resultado).isEqualTo(respostaEsperada);
        assertThat(rascunho.getStatus()).isEqualTo(StatusRascunhoIa.CONFIRMADO);
    }

    @Test
    void descartarRascunho_pendente_deveMarcarDescartado() {
        RascunhoIa rascunho = RascunhoIa.builder()
                .id(4L)
                .tipo(TipoRascunhoIa.TREINO)
                .status(StatusRascunhoIa.PENDENTE)
                .conteudoJson("{}")
                .build();
        when(rascunhoIaRepository.findByIdAndUsuarioId(4L, USUARIO_ID)).thenReturn(Optional.of(rascunho));

        iaChatService.descartarRascunho(USUARIO_ID, 4L);

        assertThat(rascunho.getStatus()).isEqualTo(StatusRascunhoIa.DESCARTADO);
        verify(rascunhoIaRepository).save(rascunho);
    }

    @Test
    void descartarRascunho_jaDescartado_deveLancarBusinessException() {
        RascunhoIa rascunho = RascunhoIa.builder()
                .id(4L)
                .tipo(TipoRascunhoIa.TREINO)
                .status(StatusRascunhoIa.DESCARTADO)
                .conteudoJson("{}")
                .build();
        when(rascunhoIaRepository.findByIdAndUsuarioId(4L, USUARIO_ID)).thenReturn(Optional.of(rascunho));

        assertThatThrownBy(() -> iaChatService.descartarRascunho(USUARIO_ID, 4L)).isInstanceOf(BusinessException.class);
    }
}
