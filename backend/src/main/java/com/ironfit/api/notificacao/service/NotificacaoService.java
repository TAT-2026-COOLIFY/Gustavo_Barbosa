package com.ironfit.api.notificacao.service;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.dieta.entity.Dieta;
import com.ironfit.api.dieta.repository.DietaRepository;
import com.ironfit.api.dieta.repository.RefeicaoRepository;
import com.ironfit.api.dieta.repository.RegistroRefeicaoRepository;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.hidratacao.dto.ResumoDiaHidratacaoResponseDTO;
import com.ironfit.api.hidratacao.service.HidratacaoService;
import com.ironfit.api.notificacao.dto.NotificacaoResponseDTO;
import com.ironfit.api.notificacao.entity.Notificacao;
import com.ironfit.api.notificacao.entity.TipoNotificacao;
import com.ironfit.api.notificacao.mapper.NotificacaoMapper;
import com.ironfit.api.notificacao.repository.NotificacaoRepository;
import com.ironfit.api.treino.repository.SessaoTreinoRepository;
import com.ironfit.api.treino.repository.TreinoRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Central de notificações in-app. Sem push do navegador nem job agendado: as notificações "de hoje"
 * são geradas sob demanda, avaliando o estado atual de água/treino/refeição/metas sempre que o usuário
 * abre a tela — cada regra só cria uma notificação por dia (dedupe via {@code existsByUsuarioIdAndTipoAndCriadoEmAfter}).
 */
@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoMapper notificacaoMapper;
    private final HidratacaoService hidratacaoService;
    private final TreinoRepository treinoRepository;
    private final SessaoTreinoRepository sessaoTreinoRepository;
    private final DietaRepository dietaRepository;
    private final RefeicaoRepository refeicaoRepository;
    private final RegistroRefeicaoRepository registroRefeicaoRepository;

    public NotificacaoService(
            NotificacaoRepository notificacaoRepository,
            UsuarioRepository usuarioRepository,
            NotificacaoMapper notificacaoMapper,
            HidratacaoService hidratacaoService,
            TreinoRepository treinoRepository,
            SessaoTreinoRepository sessaoTreinoRepository,
            DietaRepository dietaRepository,
            RefeicaoRepository refeicaoRepository,
            RegistroRefeicaoRepository registroRefeicaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacaoMapper = notificacaoMapper;
        this.hidratacaoService = hidratacaoService;
        this.treinoRepository = treinoRepository;
        this.sessaoTreinoRepository = sessaoTreinoRepository;
        this.dietaRepository = dietaRepository;
        this.refeicaoRepository = refeicaoRepository;
        this.registroRefeicaoRepository = registroRefeicaoRepository;
    }

    @Transactional
    public List<NotificacaoResponseDTO> listar(Long usuarioId) {
        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        avaliarAgua(usuarioId, usuario);
        avaliarTreino(usuarioId, usuario);
        avaliarRefeicao(usuarioId, usuario);

        return notificacaoRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId).stream()
                .map(notificacaoMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public long contarNaoLidas(Long usuarioId) {
        return notificacaoRepository.countByUsuarioIdAndLidaFalse(usuarioId);
    }

    @Transactional
    public NotificacaoResponseDTO marcarLida(Long usuarioId, Long id) {
        Notificacao notificacao = buscarDoUsuario(usuarioId, id);
        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
        return notificacaoMapper.toResponseDTO(notificacao);
    }

    @Transactional
    public void marcarTodasComoLidas(Long usuarioId) {
        notificacaoRepository.marcarTodasComoLidas(usuarioId);
    }

    @Transactional
    public void remover(Long usuarioId, Long id) {
        notificacaoRepository.delete(buscarDoUsuario(usuarioId, id));
    }

    private void avaliarAgua(Long usuarioId, Usuario usuario) {
        ResumoDiaHidratacaoResponseDTO resumo = hidratacaoService.obterResumoDia(usuarioId, LocalDate.now());
        if (resumo.totalMl() == 0) {
            criarSeNecessario(
                    usuarioId,
                    usuario,
                    TipoNotificacao.AGUA,
                    "Beba água 💧",
                    "Você ainda não registrou nenhum copo de água hoje.");
        } else if (resumo.percentual() >= 100) {
            criarSeNecessario(
                    usuarioId,
                    usuario,
                    TipoNotificacao.META,
                    "Meta batida! 🎉",
                    "Você já bateu sua meta de água hoje. Parabéns!");
        }
    }

    private void avaliarTreino(Long usuarioId, Usuario usuario) {
        if (!treinoRepository.existsByUsuarioId(usuarioId)) {
            return;
        }
        if (!sessaoTreinoRepository.existsByUsuarioIdAndConcluidoTrueAndDataExecucaoBetween(
                usuarioId, inicioDoDia(), fimDoDia())) {
            criarSeNecessario(
                    usuarioId,
                    usuario,
                    TipoNotificacao.TREINO,
                    "Hora de treinar 💪",
                    "Você ainda não concluiu nenhum treino hoje.");
        }
    }

    private void avaliarRefeicao(Long usuarioId, Usuario usuario) {
        Optional<Dieta> dietaAtiva = dietaRepository.findByUsuarioIdAndAtivaTrue(usuarioId);
        if (dietaAtiva.isEmpty()) {
            return;
        }
        boolean temRefeicoesPlanejadas =
                !refeicaoRepository.findByDietaIdOrderByOrdem(dietaAtiva.get().getId()).isEmpty();
        if (!temRefeicoesPlanejadas) {
            return;
        }
        boolean registrouAlgumaHoje =
                !registroRefeicaoRepository.findByUsuarioIdAndDataConsumo(usuarioId, LocalDate.now()).isEmpty();
        if (!registrouAlgumaHoje) {
            criarSeNecessario(
                    usuarioId,
                    usuario,
                    TipoNotificacao.REFEICAO,
                    "Não esqueça de comer 🍽️",
                    "Você ainda não registrou nenhuma refeição da sua dieta hoje.");
        }
    }

    private void criarSeNecessario(Long usuarioId, Usuario usuario, TipoNotificacao tipo, String titulo, String mensagem) {
        if (notificacaoRepository.existsByUsuarioIdAndTipoAndCriadoEmAfter(usuarioId, tipo, inicioDoDia())) {
            return;
        }
        Notificacao notificacao = Notificacao.builder()
                .usuario(usuario)
                .tipo(tipo)
                .titulo(titulo)
                .mensagem(mensagem)
                .lida(false)
                .build();
        notificacaoRepository.save(notificacao);
    }

    private Notificacao buscarDoUsuario(Long usuarioId, Long id) {
        return notificacaoRepository
                .findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));
    }

    private Instant inicioDoDia() {
        return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    private Instant fimDoDia() {
        return LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
    }
}
