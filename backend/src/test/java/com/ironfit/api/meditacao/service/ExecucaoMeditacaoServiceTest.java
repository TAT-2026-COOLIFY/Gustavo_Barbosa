package com.ironfit.api.meditacao.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.meditacao.dto.ConcluirMeditacaoRequestDTO;
import com.ironfit.api.meditacao.dto.HistoricoMeditacaoResponseDTO;
import com.ironfit.api.meditacao.entity.Meditacao;
import com.ironfit.api.meditacao.entity.SessaoMeditacao;
import com.ironfit.api.meditacao.entity.TipoMeditacao;
import com.ironfit.api.meditacao.mapper.MeditacaoMapper;
import com.ironfit.api.meditacao.repository.SessaoMeditacaoRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExecucaoMeditacaoServiceTest {

    private static final Long USUARIO_ID = 1L;
    private static final Long MEDITACAO_ID = 5L;

    @Mock
    private SessaoMeditacaoRepository sessaoMeditacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MeditacaoService meditacaoService;

    private ExecucaoMeditacaoService execucaoMeditacaoService;

    @BeforeEach
    void setUp() {
        execucaoMeditacaoService =
                new ExecucaoMeditacaoService(sessaoMeditacaoRepository, usuarioRepository, meditacaoService, new MeditacaoMapper());
    }

    private Meditacao meditacaoRespiracao() {
        return Meditacao.builder()
                .id(MEDITACAO_ID)
                .nome("Respiração 4-7-8")
                .tipo(TipoMeditacao.RESPIRACAO)
                .duracaoMinutos(5)
                .inspirarSegundos(4)
                .segurarSegundos(7)
                .expirarSegundos(8)
                .build();
    }

    @Test
    void iniciar_deveCriarSessaoComAMeditacaoEscolhida() {
        Usuario usuario = Usuario.builder().id(USUARIO_ID).build();
        when(meditacaoService.buscar(MEDITACAO_ID)).thenReturn(meditacaoRespiracao());
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(sessaoMeditacaoRepository.save(any(SessaoMeditacao.class))).thenAnswer(inv -> inv.getArgument(0));

        var resultado = execucaoMeditacaoService.iniciar(USUARIO_ID, MEDITACAO_ID);

        assertThat(resultado.meditacaoId()).isEqualTo(MEDITACAO_ID);
        assertThat(resultado.meditacaoNome()).isEqualTo("Respiração 4-7-8");
        assertThat(resultado.concluido()).isFalse();
        assertThat(resultado.inspirarSegundos()).isEqualTo(4);
    }

    @Test
    void obter_sessaoDeOutroUsuario_deveLancarResourceNotFound() {
        when(sessaoMeditacaoRepository.findByIdAndUsuarioId(9L, USUARIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> execucaoMeditacaoService.obter(USUARIO_ID, 9L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void concluir_deveMarcarConcluidoEDefinirDuracao() {
        SessaoMeditacao sessao = SessaoMeditacao.builder()
                .id(9L)
                .meditacao(meditacaoRespiracao())
                .dataRealizacao(Instant.now())
                .concluido(false)
                .build();
        when(sessaoMeditacaoRepository.findByIdAndUsuarioId(9L, USUARIO_ID)).thenReturn(Optional.of(sessao));
        when(sessaoMeditacaoRepository.save(any(SessaoMeditacao.class))).thenAnswer(inv -> inv.getArgument(0));

        var resultado = execucaoMeditacaoService.concluir(USUARIO_ID, 9L, new ConcluirMeditacaoRequestDTO(300));

        assertThat(resultado.concluido()).isTrue();
        assertThat(resultado.duracaoSegundos()).isEqualTo(300);
    }

    @Test
    void historico_deveMapearApenasSessoesRetornadasPeloRepositorio() {
        SessaoMeditacao sessao = SessaoMeditacao.builder()
                .id(1L)
                .meditacao(meditacaoRespiracao())
                .dataRealizacao(Instant.now())
                .duracaoSegundos(300)
                .concluido(true)
                .build();
        when(sessaoMeditacaoRepository.findByUsuarioIdAndConcluidoTrueOrderByDataRealizacaoDesc(USUARIO_ID))
                .thenReturn(List.of(sessao));

        List<HistoricoMeditacaoResponseDTO> historico = execucaoMeditacaoService.historico(USUARIO_ID);

        assertThat(historico).hasSize(1);
        assertThat(historico.get(0).meditacaoNome()).isEqualTo("Respiração 4-7-8");
        assertThat(historico.get(0).tipo()).isEqualTo(TipoMeditacao.RESPIRACAO);
    }

    @Test
    void sequencia_comSessoesHojeEOntem_deveContarDoisDias() {
        Instant hoje = Instant.now();
        Instant ontem = hoje.minus(1, ChronoUnit.DAYS);
        Instant anteontem = hoje.minus(3, ChronoUnit.DAYS); // fora da sequência (pulou um dia)

        SessaoMeditacao sessaoHoje =
                SessaoMeditacao.builder().meditacao(meditacaoRespiracao()).dataRealizacao(hoje).concluido(true).build();
        SessaoMeditacao sessaoOntem =
                SessaoMeditacao.builder().meditacao(meditacaoRespiracao()).dataRealizacao(ontem).concluido(true).build();
        SessaoMeditacao sessaoAnteontem =
                SessaoMeditacao.builder().meditacao(meditacaoRespiracao()).dataRealizacao(anteontem).concluido(true).build();

        when(sessaoMeditacaoRepository.findByUsuarioIdAndConcluidoTrueAndDataRealizacaoAfterOrderByDataRealizacaoDesc(
                        anyLong(), any()))
                .thenReturn(List.of(sessaoHoje, sessaoOntem, sessaoAnteontem));

        var resultado = execucaoMeditacaoService.sequencia(USUARIO_ID);

        assertThat(resultado.diasConsecutivos()).isEqualTo(2);
    }

    @Test
    void sequencia_semSessaoHoje_deveRetornarZero() {
        when(sessaoMeditacaoRepository.findByUsuarioIdAndConcluidoTrueAndDataRealizacaoAfterOrderByDataRealizacaoDesc(
                        anyLong(), any()))
                .thenReturn(List.of());

        var resultado = execucaoMeditacaoService.sequencia(USUARIO_ID);

        assertThat(resultado.diasConsecutivos()).isEqualTo(0);
    }
}
