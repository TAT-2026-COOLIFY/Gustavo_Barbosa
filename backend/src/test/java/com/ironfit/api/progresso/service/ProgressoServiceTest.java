package com.ironfit.api.progresso.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.exception.BusinessException;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.perfil.entity.Perfil;
import com.ironfit.api.perfil.repository.PerfilRepository;
import com.ironfit.api.progresso.dto.RegistrarProgressoRequestDTO;
import com.ironfit.api.progresso.dto.RegistroProgressoResponseDTO;
import com.ironfit.api.progresso.entity.RegistroProgresso;
import com.ironfit.api.progresso.mapper.RegistroProgressoMapper;
import com.ironfit.api.progresso.repository.RegistroProgressoRepository;
import com.ironfit.api.storage.FileStorageService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProgressoServiceTest {

    private static final Long USUARIO_ID = 1L;
    private static final LocalDate DATA = LocalDate.of(2026, 8, 5);

    @Mock
    private RegistroProgressoRepository registroProgressoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private FileStorageService fileStorageService;

    private ProgressoService progressoService;

    @BeforeEach
    void setUp() {
        progressoService = new ProgressoService(
                registroProgressoRepository,
                usuarioRepository,
                perfilRepository,
                new RegistroProgressoMapper(),
                fileStorageService);
    }

    @Test
    void registrar_semRegistroNaData_deveCriarNovo() {
        Usuario usuario = Usuario.builder().id(USUARIO_ID).build();
        Perfil perfil = Perfil.builder().alturaCm(BigDecimal.valueOf(180)).build();

        when(registroProgressoRepository.findByUsuarioIdAndDataRegistro(USUARIO_ID, DATA)).thenReturn(Optional.empty());
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(perfilRepository.findByUsuarioId(USUARIO_ID)).thenReturn(Optional.of(perfil));
        when(registroProgressoRepository.save(any(RegistroProgresso.class))).thenAnswer(inv -> inv.getArgument(0));

        RegistrarProgressoRequestDTO request =
                new RegistrarProgressoRequestDTO(DATA, BigDecimal.valueOf(90), null, null, null, null, null, null, null);

        RegistroProgressoResponseDTO resultado = progressoService.registrar(USUARIO_ID, request);

        assertThat(resultado.pesoKg()).isEqualByComparingTo("90");
        // 90 / (1.80*1.80) = 27.8
        assertThat(resultado.imc()).isEqualByComparingTo("27.8");
    }

    @Test
    void registrar_comRegistroExistenteNaData_deveAtualizarEmVezDeCriar() {
        RegistroProgresso existente = RegistroProgresso.builder()
                .id(9L)
                .dataRegistro(DATA)
                .pesoKg(BigDecimal.valueOf(80))
                .build();

        when(registroProgressoRepository.findByUsuarioIdAndDataRegistro(USUARIO_ID, DATA)).thenReturn(Optional.of(existente));
        when(perfilRepository.findByUsuarioId(USUARIO_ID)).thenReturn(Optional.empty());
        when(registroProgressoRepository.save(any(RegistroProgresso.class))).thenAnswer(inv -> inv.getArgument(0));

        RegistrarProgressoRequestDTO request =
                new RegistrarProgressoRequestDTO(DATA, BigDecimal.valueOf(79.5), null, null, null, null, null, null, null);

        RegistroProgressoResponseDTO resultado = progressoService.registrar(USUARIO_ID, request);

        assertThat(resultado.pesoKg()).isEqualByComparingTo("79.5");
        assertThat(resultado.imc()).isNull();
        org.mockito.Mockito.verify(usuarioRepository, org.mockito.Mockito.never()).findById(any());
    }

    @Test
    void registrar_comPercentualGordura_deveCalcularMassaMagra() {
        Usuario usuario = Usuario.builder().id(USUARIO_ID).build();
        when(registroProgressoRepository.findByUsuarioIdAndDataRegistro(USUARIO_ID, DATA)).thenReturn(Optional.empty());
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(perfilRepository.findByUsuarioId(USUARIO_ID)).thenReturn(Optional.empty());
        when(registroProgressoRepository.save(any(RegistroProgresso.class))).thenAnswer(inv -> inv.getArgument(0));

        RegistrarProgressoRequestDTO request = new RegistrarProgressoRequestDTO(
                DATA, BigDecimal.valueOf(100), BigDecimal.valueOf(20), null, null, null, null, null, null);

        RegistroProgressoResponseDTO resultado = progressoService.registrar(USUARIO_ID, request);

        assertThat(resultado.massaMagraKg()).isEqualByComparingTo("80.0");
    }

    @Test
    void obterUltimo_semRegistros_deveLancarResourceNotFound() {
        when(registroProgressoRepository.findFirstByUsuarioIdOrderByDataRegistroDesc(USUARIO_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> progressoService.obterUltimo(USUARIO_ID)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void atualizar_paraDataJaOcupadaPorOutroRegistro_deveLancarBusinessException() {
        RegistroProgresso registro = RegistroProgresso.builder().id(1L).dataRegistro(DATA).build();
        LocalDate outraData = DATA.plusDays(1);

        when(registroProgressoRepository.findByIdAndUsuarioId(1L, USUARIO_ID)).thenReturn(Optional.of(registro));
        when(registroProgressoRepository.findByUsuarioIdAndDataRegistro(USUARIO_ID, outraData))
                .thenReturn(Optional.of(RegistroProgresso.builder().id(2L).dataRegistro(outraData).build()));

        RegistrarProgressoRequestDTO request =
                new RegistrarProgressoRequestDTO(outraData, BigDecimal.valueOf(80), null, null, null, null, null, null, null);

        assertThatThrownBy(() -> progressoService.atualizar(USUARIO_ID, 1L, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void removerRegistro_naoPertenceAoUsuario_deveLancarResourceNotFound() {
        when(registroProgressoRepository.findByIdAndUsuarioId(5L, USUARIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> progressoService.removerRegistro(USUARIO_ID, 5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
