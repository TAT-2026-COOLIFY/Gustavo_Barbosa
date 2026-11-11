package com.ironfit.api.dieta.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.common.service.FavoritoService;
import com.ironfit.api.dieta.dto.DietaRequestDTO;
import com.ironfit.api.dieta.dto.MetaNutricionalCalculadaDTO;
import com.ironfit.api.dieta.entity.Dieta;
import com.ironfit.api.dieta.entity.NivelAtividade;
import com.ironfit.api.dieta.entity.OrigemDieta;
import com.ironfit.api.dieta.mapper.DietaMapper;
import com.ironfit.api.dieta.mapper.RefeicaoAlimentoMapper;
import com.ironfit.api.dieta.mapper.RefeicaoMapper;
import com.ironfit.api.dieta.repository.AlimentoRepository;
import com.ironfit.api.dieta.repository.DietaRepository;
import com.ironfit.api.dieta.repository.RefeicaoAlimentoRepository;
import com.ironfit.api.dieta.repository.RefeicaoRepository;
import com.ironfit.api.exception.BusinessException;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.exception.UnauthorizedException;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DietaServiceTest {

    private static final Long USUARIO_ID = 1L;

    @Mock
    private DietaRepository dietaRepository;

    @Mock
    private RefeicaoRepository refeicaoRepository;

    @Mock
    private RefeicaoAlimentoRepository refeicaoAlimentoRepository;

    @Mock
    private AlimentoRepository alimentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private DietaMapper dietaMapper;

    @Mock
    private RefeicaoMapper refeicaoMapper;

    @Mock
    private RefeicaoAlimentoMapper refeicaoAlimentoMapper;

    @Mock
    private FavoritoService favoritoService;

    @Mock
    private CalculoNutricionalService calculoNutricionalService;

    private DietaService dietaService;

    @BeforeEach
    void setUp() {
        dietaService = new DietaService(
                dietaRepository,
                refeicaoRepository,
                refeicaoAlimentoRepository,
                alimentoRepository,
                usuarioRepository,
                dietaMapper,
                refeicaoMapper,
                refeicaoAlimentoMapper,
                favoritoService,
                calculoNutricionalService);
    }

    @Test
    void aplicarMetas_automatico_devePreencherCamposComRetornoDoCalculo() {
        Dieta dieta = Dieta.builder().nome("Minha dieta").origem(OrigemDieta.MANUAL).build();
        DietaRequestDTO request = new DietaRequestDTO(
                "Minha dieta",
                new BigDecimal("80"),
                NivelAtividade.MODERADO,
                TipoObjetivo.HIPERTROFIA,
                true,
                null,
                null,
                null,
                null,
                null,
                List.of());

        MetaNutricionalCalculadaDTO meta = new MetaNutricionalCalculadaDTO(1780, 2759, 3000, 200, 300, 90, 2800);
        when(calculoNutricionalService.calcular(USUARIO_ID, new BigDecimal("80"), NivelAtividade.MODERADO, TipoObjetivo.HIPERTROFIA))
                .thenReturn(meta);

        dietaService.aplicarMetas(dieta, USUARIO_ID, request);

        assertThat(dieta.isCalculoAutomatico()).isTrue();
        assertThat(dieta.getMetaCaloriasKcal()).isEqualTo(3000);
        assertThat(dieta.getMetaProteinaG()).isEqualTo(200);
        assertThat(dieta.getMetaCarboidratoG()).isEqualTo(300);
        assertThat(dieta.getMetaGorduraG()).isEqualTo(90);
        assertThat(dieta.getMetaAguaMl()).isEqualTo(2800);
    }

    @Test
    void aplicarMetas_automatico_semPesoDeveLancarBusinessException() {
        Dieta dieta = Dieta.builder().nome("Minha dieta").origem(OrigemDieta.MANUAL).build();
        DietaRequestDTO request = new DietaRequestDTO(
                "Minha dieta", null, NivelAtividade.MODERADO, TipoObjetivo.HIPERTROFIA, true, null, null, null, null, null, List.of());

        assertThatThrownBy(() -> dietaService.aplicarMetas(dieta, USUARIO_ID, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void aplicarMetas_manual_devePreencherCamposInformados() {
        Dieta dieta = Dieta.builder().nome("Minha dieta").origem(OrigemDieta.MANUAL).build();
        DietaRequestDTO request =
                new DietaRequestDTO("Minha dieta", null, null, null, false, 2000, 150, 200, 60, 2500, List.of());

        dietaService.aplicarMetas(dieta, USUARIO_ID, request);

        assertThat(dieta.isCalculoAutomatico()).isFalse();
        assertThat(dieta.getMetaCaloriasKcal()).isEqualTo(2000);
        assertThat(dieta.getMetaProteinaG()).isEqualTo(150);
        assertThat(dieta.getMetaCarboidratoG()).isEqualTo(200);
        assertThat(dieta.getMetaGorduraG()).isEqualTo(60);
        assertThat(dieta.getMetaAguaMl()).isEqualTo(2500);
    }

    @Test
    void aplicarMetas_manual_semMetaCaloriasDeveLancarBusinessException() {
        Dieta dieta = Dieta.builder().nome("Minha dieta").origem(OrigemDieta.MANUAL).build();
        DietaRequestDTO request =
                new DietaRequestDTO("Minha dieta", null, null, null, false, null, 150, 200, 60, 2500, List.of());

        assertThatThrownBy(() -> dietaService.aplicarMetas(dieta, USUARIO_ID, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void buscarAcessivel_devePermitirDonoDaDieta() {
        Usuario usuario = Usuario.builder().id(USUARIO_ID).build();
        Dieta dieta = Dieta.builder().usuario(usuario).origem(OrigemDieta.MANUAL).build();
        when(dietaRepository.findById(10L)).thenReturn(Optional.of(dieta));

        assertThat(dietaService.buscarAcessivel(USUARIO_ID, 10L)).isSameAs(dieta);
    }

    @Test
    void buscarAcessivel_devePermitirDietaTemplate() {
        Dieta dieta = Dieta.builder().usuario(null).origem(OrigemDieta.TEMPLATE).build();
        when(dietaRepository.findById(10L)).thenReturn(Optional.of(dieta));

        assertThat(dietaService.buscarAcessivel(USUARIO_ID, 10L)).isSameAs(dieta);
    }

    @Test
    void buscarAcessivel_deveNegarDietaDeOutroUsuario() {
        Usuario outroUsuario = Usuario.builder().id(999L).build();
        Dieta dieta = Dieta.builder().usuario(outroUsuario).origem(OrigemDieta.MANUAL).build();
        when(dietaRepository.findById(10L)).thenReturn(Optional.of(dieta));

        assertThatThrownBy(() -> dietaService.buscarAcessivel(USUARIO_ID, 10L)).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void buscarAcessivel_deveLancarResourceNotFoundQuandoDietaNaoExiste() {
        when(dietaRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dietaService.buscarAcessivel(USUARIO_ID, 10L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void ativar_deveDesativarDemaisEAtivarDietaEscolhida() {
        Usuario usuario = Usuario.builder().id(USUARIO_ID).build();
        Dieta dieta = Dieta.builder()
                .usuario(usuario)
                .origem(OrigemDieta.MANUAL)
                .nome("Dieta X")
                .metaCaloriasKcal(2000)
                .metaProteinaG(150)
                .metaCarboidratoG(200)
                .metaGorduraG(60)
                .metaAguaMl(2500)
                .build();
        when(dietaRepository.findByIdAndUsuarioId(10L, USUARIO_ID)).thenReturn(Optional.of(dieta));
        when(refeicaoRepository.findByDietaIdOrderByOrdem(dieta.getId())).thenReturn(List.of());

        dietaService.ativar(USUARIO_ID, 10L);

        verify(dietaRepository).desativarTodasDoUsuario(USUARIO_ID);
        assertThat(dieta.isAtiva()).isTrue();
        verify(dietaRepository, never()).desativarTodasDoUsuario(999L);
    }
    @Test
    void ativar_dietaModelo_deveCriarCopiaDoUsuarioEAtivarACopia() {
        Usuario usuario = Usuario.builder().id(USUARIO_ID).build();
        Dieta modelo = Dieta.builder()
                .id(20L)
                .origem(OrigemDieta.TEMPLATE)
                .nome("Dieta pronta")
                .metaCaloriasKcal(2000)
                .metaProteinaG(150)
                .metaCarboidratoG(200)
                .metaGorduraG(60)
                .metaAguaMl(2500)
                .build();
        when(dietaRepository.findByIdAndUsuarioId(20L, USUARIO_ID)).thenReturn(Optional.empty());
        when(dietaRepository.findById(20L)).thenReturn(Optional.of(modelo));
        when(dietaRepository.findFirstByUsuarioIdAndTemplateOrigemIdOrderByCriadoEmDesc(USUARIO_ID, 20L))
                .thenReturn(Optional.empty());
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(refeicaoRepository.findByDietaIdOrderByOrdem(any())).thenReturn(List.of());

        dietaService.ativar(USUARIO_ID, 20L);

        var captor = org.mockito.ArgumentCaptor.forClass(Dieta.class);
        verify(dietaRepository, org.mockito.Mockito.atLeastOnce()).save(captor.capture());
        Dieta copia = captor.getValue();
        assertThat(copia).isNotSameAs(modelo);
        assertThat(copia.getUsuario()).isEqualTo(usuario);
        assertThat(copia.getNome()).isEqualTo("Dieta pronta");
        assertThat(copia.getTemplateOrigemId()).isEqualTo(20L);
        assertThat(copia.isAtiva()).isTrue();
        assertThat(modelo.isAtiva()).isFalse();
        verify(dietaRepository).desativarTodasDoUsuario(USUARIO_ID);
    }

    @Test
    void ativar_dietaModeloJaAtivadaAntes_deveReaproveitarCopiaExistente() {
        Usuario usuario = Usuario.builder().id(USUARIO_ID).build();
        Dieta modelo = Dieta.builder().id(20L).origem(OrigemDieta.TEMPLATE).nome("Dieta pronta").build();
        Dieta copiaExistente = Dieta.builder()
                .id(30L)
                .usuario(usuario)
                .origem(OrigemDieta.MANUAL)
                .templateOrigemId(20L)
                .nome("Dieta pronta")
                .build();
        when(dietaRepository.findByIdAndUsuarioId(20L, USUARIO_ID)).thenReturn(Optional.empty());
        when(dietaRepository.findById(20L)).thenReturn(Optional.of(modelo));
        when(dietaRepository.findFirstByUsuarioIdAndTemplateOrigemIdOrderByCriadoEmDesc(USUARIO_ID, 20L))
                .thenReturn(Optional.of(copiaExistente));
        when(refeicaoRepository.findByDietaIdOrderByOrdem(30L)).thenReturn(List.of());

        dietaService.ativar(USUARIO_ID, 20L);

        assertThat(copiaExistente.isAtiva()).isTrue();
        verify(dietaRepository).save(copiaExistente);
        verify(usuarioRepository, never()).findById(any());
    }
    @Test
    void ativar_dietaJaAtiva_naoDeveDesativarNada() {
        Usuario usuario = Usuario.builder().id(USUARIO_ID).build();
        Dieta dieta = Dieta.builder().id(10L).usuario(usuario).origem(OrigemDieta.MANUAL).nome("Dieta X").ativa(true).build();
        when(dietaRepository.findByIdAndUsuarioId(10L, USUARIO_ID)).thenReturn(Optional.of(dieta));
        when(refeicaoRepository.findByDietaIdOrderByOrdem(10L)).thenReturn(List.of());

        dietaService.ativar(USUARIO_ID, 10L);

        assertThat(dieta.isAtiva()).isTrue();
        verify(dietaRepository, never()).desativarTodasDoUsuario(any());
    }
}
