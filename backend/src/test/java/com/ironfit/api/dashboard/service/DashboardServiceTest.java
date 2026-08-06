package com.ironfit.api.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.ironfit.api.dashboard.dto.ConquistaDTO;
import com.ironfit.api.dashboard.dto.DashboardResponseDTO;
import com.ironfit.api.dieta.dto.DiarioDiaResponseDTO;
import com.ironfit.api.dieta.service.DiarioService;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.hidratacao.dto.ResumoDiaHidratacaoResponseDTO;
import com.ironfit.api.hidratacao.dto.SequenciaHidratacaoResponseDTO;
import com.ironfit.api.hidratacao.service.HidratacaoService;
import com.ironfit.api.ia.entity.StatusRascunhoIa;
import com.ironfit.api.ia.repository.RascunhoIaRepository;
import com.ironfit.api.meditacao.dto.SequenciaMeditacaoResponseDTO;
import com.ironfit.api.meditacao.service.ExecucaoMeditacaoService;
import com.ironfit.api.notificacao.service.NotificacaoService;
import com.ironfit.api.perfil.dto.ObjetivoResponseDTO;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import com.ironfit.api.perfil.service.ObjetivoService;
import com.ironfit.api.progresso.dto.RegistroProgressoResponseDTO;
import com.ironfit.api.progresso.service.ProgressoService;
import com.ironfit.api.treino.dto.VolumeSemanalResponseDTO;
import com.ironfit.api.treino.repository.SessaoTreinoRepository;
import com.ironfit.api.treino.service.ExecucaoTreinoService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    private static final Long USUARIO_ID = 1L;

    @Mock
    private ProgressoService progressoService;

    @Mock
    private ObjetivoService objetivoService;

    @Mock
    private HidratacaoService hidratacaoService;

    @Mock
    private SessaoTreinoRepository sessaoTreinoRepository;

    @Mock
    private ExecucaoTreinoService execucaoTreinoService;

    @Mock
    private DiarioService diarioService;

    @Mock
    private ExecucaoMeditacaoService execucaoMeditacaoService;

    @Mock
    private NotificacaoService notificacaoService;

    @Mock
    private RascunhoIaRepository rascunhoIaRepository;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(
                progressoService,
                objetivoService,
                hidratacaoService,
                sessaoTreinoRepository,
                execucaoTreinoService,
                diarioService,
                execucaoMeditacaoService,
                notificacaoService,
                rascunhoIaRepository);
    }

    @Test
    void montar_usuarioComDadosEmTodosOsModulos_devePreencherTodosOsCampos() {
        when(progressoService.obterUltimo(USUARIO_ID))
                .thenReturn(new RegistroProgressoResponseDTO(
                        10L,
                        LocalDate.now(),
                        new BigDecimal("80.0"),
                        new BigDecimal("24.5"),
                        new BigDecimal("18.0"),
                        new BigDecimal("65.5"),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        when(objetivoService.obterAtivo(USUARIO_ID))
                .thenReturn(new ObjetivoResponseDTO(
                        5L, TipoObjetivo.HIPERTROFIA, new BigDecimal("85.0"), LocalDate.now(), null, true));
        when(hidratacaoService.obterResumoDia(eq(USUARIO_ID), any(LocalDate.class)))
                .thenReturn(new ResumoDiaHidratacaoResponseDTO(LocalDate.now(), 2000, 2000, 100, java.util.List.of()));
        when(hidratacaoService.calcularSequencia(USUARIO_ID)).thenReturn(new SequenciaHidratacaoResponseDTO(5));
        when(sessaoTreinoRepository.existsByUsuarioIdAndConcluidoTrueAndDataExecucaoBetween(
                        eq(USUARIO_ID), any(Instant.class), any(Instant.class)))
                .thenReturn(true);
        when(execucaoTreinoService.volumeSemanal(USUARIO_ID)).thenReturn(new VolumeSemanalResponseDTO(new BigDecimal("1200")));
        when(diarioService.obterDia(eq(USUARIO_ID), any(LocalDate.class)))
                .thenReturn(new DiarioDiaResponseDTO(
                        LocalDate.now(),
                        1L,
                        "Dieta Hipertrofia",
                        2800,
                        180,
                        320,
                        80,
                        new BigDecimal("1500"),
                        new BigDecimal("100"),
                        new BigDecimal("150"),
                        new BigDecimal("40"),
                        java.util.List.of()));
        when(execucaoMeditacaoService.sequencia(USUARIO_ID)).thenReturn(new SequenciaMeditacaoResponseDTO(4));
        when(notificacaoService.contarNaoLidas(USUARIO_ID)).thenReturn(3L);
        when(rascunhoIaRepository.existsByUsuarioIdAndStatus(USUARIO_ID, StatusRascunhoIa.PENDENTE)).thenReturn(true);

        DashboardResponseDTO resultado = dashboardService.montar(USUARIO_ID);

        assertThat(resultado.imc().imc()).isEqualByComparingTo("24.5");
        assertThat(resultado.objetivoAtivo().tipo()).isEqualTo(TipoObjetivo.HIPERTROFIA);
        assertThat(resultado.agua().diasConsecutivos()).isEqualTo(5);
        assertThat(resultado.treino().treinouHoje()).isTrue();
        assertThat(resultado.treino().volumeSemanalKg()).isEqualByComparingTo("1200");
        assertThat(resultado.dieta().temDietaAtiva()).isTrue();
        assertThat(resultado.dieta().dietaNome()).isEqualTo("Dieta Hipertrofia");
        assertThat(resultado.meditacao().diasConsecutivos()).isEqualTo(4);
        assertThat(resultado.notificacoesNaoLidas()).isEqualTo(3L);
        assertThat(resultado.rascunhoIaPendente()).isTrue();
        // sequência de água >=3, meta de água 100%, treinou hoje, sequência de meditação >=3, progresso de hoje
        assertThat(resultado.conquistas()).hasSize(5);
    }

    @Test
    void montar_usuarioNovoSemProgressoObjetivoOuDieta_naoDeveLancarExcecao() {
        when(progressoService.obterUltimo(USUARIO_ID)).thenThrow(new ResourceNotFoundException("Nenhum registro"));
        when(objetivoService.obterAtivo(USUARIO_ID)).thenThrow(new ResourceNotFoundException("Nenhum objetivo ativo"));
        when(hidratacaoService.obterResumoDia(eq(USUARIO_ID), any(LocalDate.class)))
                .thenReturn(new ResumoDiaHidratacaoResponseDTO(LocalDate.now(), 2000, 0, 0, java.util.List.of()));
        when(hidratacaoService.calcularSequencia(USUARIO_ID)).thenReturn(new SequenciaHidratacaoResponseDTO(0));
        when(sessaoTreinoRepository.existsByUsuarioIdAndConcluidoTrueAndDataExecucaoBetween(
                        eq(USUARIO_ID), any(Instant.class), any(Instant.class)))
                .thenReturn(false);
        when(execucaoTreinoService.volumeSemanal(USUARIO_ID)).thenReturn(new VolumeSemanalResponseDTO(BigDecimal.ZERO));
        when(diarioService.obterDia(eq(USUARIO_ID), any(LocalDate.class)))
                .thenThrow(new ResourceNotFoundException("Nenhuma dieta ativa"));
        when(execucaoMeditacaoService.sequencia(USUARIO_ID)).thenReturn(new SequenciaMeditacaoResponseDTO(0));
        when(notificacaoService.contarNaoLidas(USUARIO_ID)).thenReturn(0L);
        when(rascunhoIaRepository.existsByUsuarioIdAndStatus(USUARIO_ID, StatusRascunhoIa.PENDENTE)).thenReturn(false);

        DashboardResponseDTO resultado = dashboardService.montar(USUARIO_ID);

        assertThat(resultado.imc()).isNull();
        assertThat(resultado.objetivoAtivo()).isNull();
        assertThat(resultado.dieta().temDietaAtiva()).isFalse();
        assertThat(resultado.dieta().dietaNome()).isNull();
        assertThat(resultado.dieta().metaCaloriasKcal()).isNull();
        assertThat(resultado.rascunhoIaPendente()).isFalse();
        assertThat(resultado.conquistas()).isEmpty();
    }

    @Test
    void calcularConquistas_apenasTreinouHoje_deveRetornarSoAConquistaDeTreino() {
        when(progressoService.obterUltimo(USUARIO_ID)).thenThrow(new ResourceNotFoundException("Nenhum registro"));
        when(objetivoService.obterAtivo(USUARIO_ID)).thenThrow(new ResourceNotFoundException("Nenhum objetivo ativo"));
        when(hidratacaoService.obterResumoDia(eq(USUARIO_ID), any(LocalDate.class)))
                .thenReturn(new ResumoDiaHidratacaoResponseDTO(LocalDate.now(), 2000, 500, 25, java.util.List.of()));
        when(hidratacaoService.calcularSequencia(USUARIO_ID)).thenReturn(new SequenciaHidratacaoResponseDTO(1));
        when(sessaoTreinoRepository.existsByUsuarioIdAndConcluidoTrueAndDataExecucaoBetween(
                        eq(USUARIO_ID), any(Instant.class), any(Instant.class)))
                .thenReturn(true);
        when(execucaoTreinoService.volumeSemanal(USUARIO_ID)).thenReturn(new VolumeSemanalResponseDTO(new BigDecimal("300")));
        when(diarioService.obterDia(eq(USUARIO_ID), any(LocalDate.class)))
                .thenThrow(new ResourceNotFoundException("Nenhuma dieta ativa"));
        when(execucaoMeditacaoService.sequencia(USUARIO_ID)).thenReturn(new SequenciaMeditacaoResponseDTO(0));
        when(notificacaoService.contarNaoLidas(USUARIO_ID)).thenReturn(0L);
        when(rascunhoIaRepository.existsByUsuarioIdAndStatus(USUARIO_ID, StatusRascunhoIa.PENDENTE)).thenReturn(false);

        DashboardResponseDTO resultado = dashboardService.montar(USUARIO_ID);

        assertThat(resultado.conquistas()).hasSize(1);
        ConquistaDTO conquista = resultado.conquistas().get(0);
        assertThat(conquista.titulo()).isEqualTo("Treino do dia");
    }
}
