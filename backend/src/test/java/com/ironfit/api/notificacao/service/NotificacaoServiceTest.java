package com.ironfit.api.notificacao.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.dieta.entity.Dieta;
import com.ironfit.api.dieta.entity.Refeicao;
import com.ironfit.api.dieta.repository.DietaRepository;
import com.ironfit.api.dieta.repository.RefeicaoRepository;
import com.ironfit.api.dieta.repository.RegistroRefeicaoRepository;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.hidratacao.dto.ResumoDiaHidratacaoResponseDTO;
import com.ironfit.api.hidratacao.service.HidratacaoService;
import com.ironfit.api.notificacao.entity.Notificacao;
import com.ironfit.api.notificacao.entity.TipoNotificacao;
import com.ironfit.api.notificacao.mapper.NotificacaoMapper;
import com.ironfit.api.notificacao.repository.NotificacaoRepository;
import com.ironfit.api.treino.repository.SessaoTreinoRepository;
import com.ironfit.api.treino.repository.TreinoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    private static final Long USUARIO_ID = 1L;

    @Mock
    private NotificacaoRepository notificacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HidratacaoService hidratacaoService;

    @Mock
    private TreinoRepository treinoRepository;

    @Mock
    private SessaoTreinoRepository sessaoTreinoRepository;

    @Mock
    private DietaRepository dietaRepository;

    @Mock
    private RefeicaoRepository refeicaoRepository;

    @Mock
    private RegistroRefeicaoRepository registroRefeicaoRepository;

    private NotificacaoService notificacaoService;

    @BeforeEach
    void setUp() {
        notificacaoService = new NotificacaoService(
                notificacaoRepository,
                usuarioRepository,
                new NotificacaoMapper(),
                hidratacaoService,
                treinoRepository,
                sessaoTreinoRepository,
                dietaRepository,
                refeicaoRepository,
                registroRefeicaoRepository);

        lenient()
                .when(usuarioRepository.findById(USUARIO_ID))
                .thenReturn(Optional.of(Usuario.builder().id(USUARIO_ID).build()));
    }

    private ResumoDiaHidratacaoResponseDTO resumoAgua(int totalMl, int percentual) {
        return new ResumoDiaHidratacaoResponseDTO(LocalDate.now(), 2000, totalMl, percentual, List.of());
    }

    @Test
    void listar_semRegistroDeAguaHoje_deveCriarNotificacaoDeAgua() {
        when(hidratacaoService.obterResumoDia(eq(USUARIO_ID), any())).thenReturn(resumoAgua(0, 0));

        notificacaoService.listar(USUARIO_ID);

        verify(notificacaoRepository)
                .save(org.mockito.ArgumentMatchers.argThat(n -> n.getTipo() == TipoNotificacao.AGUA));
    }

    @Test
    void listar_comMetaDeAguaBatida_deveCriarNotificacaoDeMeta() {
        when(hidratacaoService.obterResumoDia(eq(USUARIO_ID), any())).thenReturn(resumoAgua(2000, 100));

        notificacaoService.listar(USUARIO_ID);

        verify(notificacaoRepository)
                .save(org.mockito.ArgumentMatchers.argThat(n -> n.getTipo() == TipoNotificacao.META));
    }

    @Test
    void listar_comAguaEntreZeroECemPorCento_naoDeveCriarNadaSobreAgua() {
        when(hidratacaoService.obterResumoDia(eq(USUARIO_ID), any())).thenReturn(resumoAgua(500, 25));

        notificacaoService.listar(USUARIO_ID);

        verify(notificacaoRepository, never()).save(any());
    }

    @Test
    void listar_semTreinosCadastrados_naoDeveCriarNotificacaoDeTreino() {
        when(hidratacaoService.obterResumoDia(eq(USUARIO_ID), any())).thenReturn(resumoAgua(500, 25));
        when(treinoRepository.existsByUsuarioId(USUARIO_ID)).thenReturn(false);

        notificacaoService.listar(USUARIO_ID);

        verify(notificacaoRepository, never())
                .save(org.mockito.ArgumentMatchers.argThat(n -> n.getTipo() == TipoNotificacao.TREINO));
    }

    @Test
    void listar_comTreinosMasSemSessaoConcluidaHoje_deveCriarNotificacaoDeTreino() {
        when(hidratacaoService.obterResumoDia(eq(USUARIO_ID), any())).thenReturn(resumoAgua(500, 25));
        when(treinoRepository.existsByUsuarioId(USUARIO_ID)).thenReturn(true);
        when(sessaoTreinoRepository.existsByUsuarioIdAndConcluidoTrueAndDataExecucaoBetween(anyLong(), any(), any()))
                .thenReturn(false);

        notificacaoService.listar(USUARIO_ID);

        verify(notificacaoRepository)
                .save(org.mockito.ArgumentMatchers.argThat(n -> n.getTipo() == TipoNotificacao.TREINO));
    }

    @Test
    void listar_comDietaAtivaSemRefeicaoRegistradaHoje_deveCriarNotificacaoDeRefeicao() {
        when(hidratacaoService.obterResumoDia(eq(USUARIO_ID), any())).thenReturn(resumoAgua(500, 25));
        Dieta dieta = Dieta.builder().id(3L).build();
        when(dietaRepository.findByUsuarioIdAndAtivaTrue(USUARIO_ID)).thenReturn(Optional.of(dieta));
        when(refeicaoRepository.findByDietaIdOrderByOrdem(3L)).thenReturn(List.of(Refeicao.builder().id(1L).build()));
        when(registroRefeicaoRepository.findByUsuarioIdAndDataConsumo(eq(USUARIO_ID), any())).thenReturn(List.of());

        notificacaoService.listar(USUARIO_ID);

        verify(notificacaoRepository)
                .save(org.mockito.ArgumentMatchers.argThat(n -> n.getTipo() == TipoNotificacao.REFEICAO));
    }

    @Test
    void listar_notificacaoJaGeradaHoje_naoDeveDuplicar() {
        when(hidratacaoService.obterResumoDia(eq(USUARIO_ID), any())).thenReturn(resumoAgua(0, 0));
        when(notificacaoRepository.existsByUsuarioIdAndTipoAndCriadoEmAfter(eq(USUARIO_ID), eq(TipoNotificacao.AGUA), any()))
                .thenReturn(true);

        notificacaoService.listar(USUARIO_ID);

        verify(notificacaoRepository, never()).save(any());
    }

    @Test
    void marcarLida_deveAtualizarStatusParaLida() {
        Notificacao notificacao = Notificacao.builder().id(5L).tipo(TipoNotificacao.AGUA).lida(false).build();
        when(notificacaoRepository.findByIdAndUsuarioId(5L, USUARIO_ID)).thenReturn(Optional.of(notificacao));

        var resultado = notificacaoService.marcarLida(USUARIO_ID, 5L);

        assertThat(resultado.lida()).isTrue();
        verify(notificacaoRepository).save(notificacao);
    }

    @Test
    void remover_notificacaoInexistenteOuDeOutroUsuario_deveLancarResourceNotFound() {
        when(notificacaoRepository.findByIdAndUsuarioId(9L, USUARIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificacaoService.remover(USUARIO_ID, 9L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void marcarTodasComoLidas_devePropagarParaRepositorio() {
        notificacaoService.marcarTodasComoLidas(USUARIO_ID);

        verify(notificacaoRepository, times(1)).marcarTodasComoLidas(USUARIO_ID);
    }
}
