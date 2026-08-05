package com.ironfit.api.hidratacao.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.hidratacao.dto.AtualizarMetaHidratacaoRequestDTO;
import com.ironfit.api.hidratacao.dto.HistoricoDiaHidratacaoDTO;
import com.ironfit.api.hidratacao.dto.RegistroHidratacaoRequestDTO;
import com.ironfit.api.hidratacao.dto.ResumoDiaHidratacaoResponseDTO;
import com.ironfit.api.hidratacao.dto.SequenciaHidratacaoResponseDTO;
import com.ironfit.api.hidratacao.entity.MetaHidratacao;
import com.ironfit.api.hidratacao.entity.RegistroHidratacao;
import com.ironfit.api.hidratacao.mapper.HidratacaoMapper;
import com.ironfit.api.hidratacao.repository.MetaHidratacaoRepository;
import com.ironfit.api.hidratacao.repository.RegistroHidratacaoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HidratacaoServiceTest {

    private static final Long USUARIO_ID = 1L;
    private static final LocalDate HOJE = LocalDate.of(2026, 8, 5);

    @Mock
    private MetaHidratacaoRepository metaHidratacaoRepository;

    @Mock
    private RegistroHidratacaoRepository registroHidratacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private HidratacaoService hidratacaoService;

    @BeforeEach
    void setUp() {
        hidratacaoService = new HidratacaoService(
                metaHidratacaoRepository, registroHidratacaoRepository, usuarioRepository, new HidratacaoMapper());
    }

    @Test
    void obterMetaAtiva_semMetaCadastrada_deveCriarMetaPadraoDe2000ml() {
        Usuario usuario = Usuario.builder().id(USUARIO_ID).build();
        when(metaHidratacaoRepository.findByUsuarioIdAndAtivoTrue(USUARIO_ID)).thenReturn(Optional.empty());
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(metaHidratacaoRepository.save(any(MetaHidratacao.class))).thenAnswer(invocation -> {
            MetaHidratacao meta = invocation.getArgument(0);
            meta.setId(10L);
            return meta;
        });

        var resultado = hidratacaoService.obterMetaAtiva(USUARIO_ID);

        assertThat(resultado.metaMl()).isEqualTo(2000);
        verify(metaHidratacaoRepository).save(any(MetaHidratacao.class));
    }

    @Test
    void atualizarMeta_deveDesativarMetaAnteriorECriarNova() {
        Usuario usuario = Usuario.builder().id(USUARIO_ID).build();
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(metaHidratacaoRepository.save(any(MetaHidratacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var resultado = hidratacaoService.atualizarMeta(USUARIO_ID, new AtualizarMetaHidratacaoRequestDTO(3000));

        assertThat(resultado.metaMl()).isEqualTo(3000);
        verify(metaHidratacaoRepository).desativarTodasDoUsuario(USUARIO_ID);
    }

    @Test
    void registrar_deveSomarNovoRegistroAoTotalDoDia() {
        Usuario usuario = Usuario.builder().id(USUARIO_ID).build();
        MetaHidratacao meta = MetaHidratacao.builder().id(5L).metaMl(2000).ativo(true).build();
        LocalDate hoje = LocalDate.now();

        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(metaHidratacaoRepository.findByUsuarioIdAndAtivoTrue(USUARIO_ID)).thenReturn(Optional.of(meta));
        when(registroHidratacaoRepository.save(any(RegistroHidratacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(registroHidratacaoRepository.findByUsuarioIdAndDataRegistroOrderByRegistradoEmDesc(USUARIO_ID, hoje))
                .thenReturn(List.of(RegistroHidratacao.builder()
                        .quantidadeMl(250)
                        .dataRegistro(hoje)
                        .build()));

        ResumoDiaHidratacaoResponseDTO resumo =
                hidratacaoService.registrar(USUARIO_ID, new RegistroHidratacaoRequestDTO(250));

        assertThat(resumo.totalMl()).isEqualTo(250);
        assertThat(resumo.metaMl()).isEqualTo(2000);
        assertThat(resumo.percentual()).isEqualTo(13);
        verify(registroHidratacaoRepository).save(any(RegistroHidratacao.class));
    }

    @Test
    void obterResumoDia_totalIgualOuMaiorQueMeta_percentualDeveSerLimitadoA100() {
        MetaHidratacao meta = MetaHidratacao.builder().id(5L).metaMl(2000).ativo(true).build();
        when(metaHidratacaoRepository.findByUsuarioIdAndAtivoTrue(USUARIO_ID)).thenReturn(Optional.of(meta));
        when(registroHidratacaoRepository.findByUsuarioIdAndDataRegistroOrderByRegistradoEmDesc(USUARIO_ID, HOJE))
                .thenReturn(List.of(
                        RegistroHidratacao.builder().quantidadeMl(1500).dataRegistro(HOJE).build(),
                        RegistroHidratacao.builder().quantidadeMl(1000).dataRegistro(HOJE).build()));

        ResumoDiaHidratacaoResponseDTO resumo = hidratacaoService.obterResumoDia(USUARIO_ID, HOJE);

        assertThat(resumo.totalMl()).isEqualTo(2500);
        assertThat(resumo.percentual()).isEqualTo(100);
    }

    @Test
    void removerRegistro_naoEncontrado_deveLancarResourceNotFound() {
        when(registroHidratacaoRepository.findByIdAndUsuarioId(99L, USUARIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hidratacaoService.removerRegistro(USUARIO_ID, 99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(registroHidratacaoRepository, never()).delete(any());
    }

    @Test
    void historico_deveMarcarDiasQueAtingiramAMeta() {
        MetaHidratacao meta = MetaHidratacao.builder().id(5L).metaMl(2000).ativo(true).build();
        LocalDate ontem = HOJE.minusDays(1);

        when(metaHidratacaoRepository.findByUsuarioIdAndAtivoTrue(USUARIO_ID)).thenReturn(Optional.of(meta));
        when(registroHidratacaoRepository.findByUsuarioIdAndDataRegistroBetween(anyLong(), any(), any()))
                .thenReturn(List.of(
                        RegistroHidratacao.builder().quantidadeMl(2000).dataRegistro(ontem).build(),
                        RegistroHidratacao.builder().quantidadeMl(500).dataRegistro(HOJE).build()));

        List<HistoricoDiaHidratacaoDTO> historico = hidratacaoService.historico(USUARIO_ID, 2);

        assertThat(historico).hasSize(2);
        HistoricoDiaHidratacaoDTO diaOntem =
                historico.stream().filter(d -> d.data().equals(ontem)).findFirst().orElseThrow();
        HistoricoDiaHidratacaoDTO diaHoje =
                historico.stream().filter(d -> d.data().equals(HOJE)).findFirst().orElseThrow();

        assertThat(diaOntem.atingiu()).isTrue();
        assertThat(diaHoje.atingiu()).isFalse();
    }

    @Test
    void calcularSequencia_diasConsecutivosAtingindoAMeta_deveContarCorretamente() {
        MetaHidratacao meta = MetaHidratacao.builder().id(5L).metaMl(2000).ativo(true).build();
        LocalDate hoje = LocalDate.now();
        LocalDate ontem = hoje.minusDays(1);
        LocalDate anteontem = hoje.minusDays(2);

        when(metaHidratacaoRepository.findByUsuarioIdAndAtivoTrue(USUARIO_ID)).thenReturn(Optional.of(meta));
        when(registroHidratacaoRepository.findByUsuarioIdAndDataRegistroBetween(anyLong(), any(), any()))
                .thenReturn(List.of(
                        RegistroHidratacao.builder().quantidadeMl(2000).dataRegistro(hoje).build(),
                        RegistroHidratacao.builder().quantidadeMl(2000).dataRegistro(ontem).build(),
                        RegistroHidratacao.builder().quantidadeMl(500).dataRegistro(anteontem).build()));

        SequenciaHidratacaoResponseDTO resultado = hidratacaoService.calcularSequencia(USUARIO_ID);

        assertThat(resultado.diasConsecutivos()).isEqualTo(2);
    }
}
