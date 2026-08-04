package com.ironfit.api.dieta.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.dieta.dto.DiarioRefeicaoDTO;
import com.ironfit.api.dieta.dto.RefeicaoResponseDTO;
import com.ironfit.api.dieta.entity.Dieta;
import com.ironfit.api.dieta.entity.OrigemDieta;
import com.ironfit.api.dieta.entity.Refeicao;
import com.ironfit.api.dieta.entity.RegistroRefeicao;
import com.ironfit.api.dieta.mapper.RefeicaoAlimentoMapper;
import com.ironfit.api.dieta.mapper.RefeicaoMapper;
import com.ironfit.api.dieta.repository.DietaRepository;
import com.ironfit.api.dieta.repository.RefeicaoAlimentoRepository;
import com.ironfit.api.dieta.repository.RefeicaoRepository;
import com.ironfit.api.dieta.repository.RegistroRefeicaoRepository;
import com.ironfit.api.exception.UnauthorizedException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiarioServiceTest {

    private static final Long USUARIO_ID = 1L;
    private static final Long REFEICAO_ID = 20L;
    private static final LocalDate DATA = LocalDate.of(2026, 8, 4);

    @Mock
    private DietaRepository dietaRepository;

    @Mock
    private RefeicaoRepository refeicaoRepository;

    @Mock
    private RefeicaoAlimentoRepository refeicaoAlimentoRepository;

    @Mock
    private RegistroRefeicaoRepository registroRefeicaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RefeicaoMapper refeicaoMapper;

    @Mock
    private RefeicaoAlimentoMapper refeicaoAlimentoMapper;

    private DiarioService diarioService;

    @BeforeEach
    void setUp() {
        diarioService = new DiarioService(
                dietaRepository,
                refeicaoRepository,
                refeicaoAlimentoRepository,
                registroRefeicaoRepository,
                usuarioRepository,
                refeicaoMapper,
                refeicaoAlimentoMapper);
    }

    @Test
    void registrarConsumo_semRegistroExistente_deveCriarNovoRegistro() {
        Dieta dietaAtiva = Dieta.builder().id(5L).origem(OrigemDieta.MANUAL).build();
        Refeicao refeicao = Refeicao.builder().id(REFEICAO_ID).dieta(dietaAtiva).nome("Almoço").build();
        Usuario usuario = Usuario.builder().id(USUARIO_ID).build();

        when(refeicaoRepository.findById(REFEICAO_ID)).thenReturn(Optional.of(refeicao));
        when(dietaRepository.findByUsuarioIdAndAtivaTrue(USUARIO_ID)).thenReturn(Optional.of(dietaAtiva));
        when(registroRefeicaoRepository.findByUsuarioIdAndRefeicaoIdAndDataConsumo(USUARIO_ID, REFEICAO_ID, DATA))
                .thenReturn(Optional.empty());
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(registroRefeicaoRepository.save(any(RegistroRefeicao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(refeicaoAlimentoRepository.findByRefeicaoIdOrderByOrdem(REFEICAO_ID)).thenReturn(List.of());
        when(refeicaoMapper.toResponseDTO(refeicao, List.of())).thenReturn(refeicaoSemNutrientes(refeicao));

        DiarioRefeicaoDTO resultado = diarioService.registrarConsumo(USUARIO_ID, REFEICAO_ID, DATA);

        assertThat(resultado.consumida()).isTrue();
        verify(registroRefeicaoRepository).save(any(RegistroRefeicao.class));
    }

    @Test
    void registrarConsumo_comRegistroExistente_deveSerIdempotente() {
        Dieta dietaAtiva = Dieta.builder().id(5L).origem(OrigemDieta.MANUAL).build();
        Refeicao refeicao = Refeicao.builder().id(REFEICAO_ID).dieta(dietaAtiva).nome("Almoço").build();
        Instant consumidoEm = Instant.parse("2026-08-04T12:00:00Z");
        RegistroRefeicao registroExistente = RegistroRefeicao.builder()
                .refeicao(refeicao)
                .dataConsumo(DATA)
                .consumidoEm(consumidoEm)
                .build();

        when(refeicaoRepository.findById(REFEICAO_ID)).thenReturn(Optional.of(refeicao));
        when(dietaRepository.findByUsuarioIdAndAtivaTrue(USUARIO_ID)).thenReturn(Optional.of(dietaAtiva));
        when(registroRefeicaoRepository.findByUsuarioIdAndRefeicaoIdAndDataConsumo(USUARIO_ID, REFEICAO_ID, DATA))
                .thenReturn(Optional.of(registroExistente));
        when(refeicaoAlimentoRepository.findByRefeicaoIdOrderByOrdem(REFEICAO_ID)).thenReturn(List.of());
        when(refeicaoMapper.toResponseDTO(refeicao, List.of())).thenReturn(refeicaoSemNutrientes(refeicao));

        DiarioRefeicaoDTO resultado = diarioService.registrarConsumo(USUARIO_ID, REFEICAO_ID, DATA);

        assertThat(resultado.consumida()).isTrue();
        assertThat(resultado.consumidoEm()).isEqualTo(consumidoEm);
        verify(registroRefeicaoRepository, never()).save(any());
        verify(usuarioRepository, never()).findById(any());
    }

    @Test
    void registrarConsumo_refeicaoForaDaDietaAtiva_deveLancarUnauthorized() {
        Dieta dietaAtiva = Dieta.builder().id(5L).origem(OrigemDieta.MANUAL).build();
        Dieta outraDieta = Dieta.builder().id(6L).origem(OrigemDieta.MANUAL).build();
        Refeicao refeicao = Refeicao.builder().id(REFEICAO_ID).dieta(outraDieta).nome("Almoço").build();

        when(refeicaoRepository.findById(REFEICAO_ID)).thenReturn(Optional.of(refeicao));
        when(dietaRepository.findByUsuarioIdAndAtivaTrue(USUARIO_ID)).thenReturn(Optional.of(dietaAtiva));

        assertThatThrownBy(() -> diarioService.registrarConsumo(USUARIO_ID, REFEICAO_ID, DATA))
                .isInstanceOf(UnauthorizedException.class);
    }

    private RefeicaoResponseDTO refeicaoSemNutrientes(Refeicao refeicao) {
        return new RefeicaoResponseDTO(
                refeicao.getId(),
                refeicao.getNome(),
                refeicao.getOrdem(),
                refeicao.getHorarioSugerido(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                List.of());
    }
}
