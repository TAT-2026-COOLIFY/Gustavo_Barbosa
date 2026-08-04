package com.ironfit.api.dieta.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ironfit.api.dieta.dto.MetaNutricionalCalculadaDTO;
import com.ironfit.api.dieta.entity.NivelAtividade;
import com.ironfit.api.dieta.service.estrategia.EstrategiaMetaNutricional;
import com.ironfit.api.dieta.service.estrategia.EstrategiaMetaNutricionalFactory;
import com.ironfit.api.exception.BusinessException;
import com.ironfit.api.perfil.dto.PerfilResponseDTO;
import com.ironfit.api.perfil.entity.NivelExperiencia;
import com.ironfit.api.perfil.entity.Sexo;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import com.ironfit.api.perfil.service.PerfilService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalculoNutricionalServiceTest {

    @Mock
    private PerfilService perfilService;

    @Mock
    private EstrategiaMetaNutricionalFactory estrategiaFactory;

    @Mock
    private EstrategiaMetaNutricional estrategia;

    private CalculoNutricionalService service;

    private static final Long USUARIO_ID = 1L;

    @Test
    void deveCalcularTmbEGetComFormulaMifflinStJeorEDelegarParaEstrategia() {
        service = new CalculoNutricionalService(perfilService, estrategiaFactory);

        PerfilResponseDTO perfil = new PerfilResponseDTO(
                "Fulano",
                "fulano@ironfit.com",
                null,
                LocalDate.now().minusYears(30),
                Sexo.MASCULINO,
                new BigDecimal("180"),
                NivelExperiencia.INTERMEDIARIO,
                null);
        when(perfilService.obterPerfil(USUARIO_ID)).thenReturn(perfil);

        MetaNutricionalCalculadaDTO metaEsperada = new MetaNutricionalCalculadaDTO(1780, 2759, 3000, 200, 300, 90, 2800);
        when(estrategiaFactory.obter(TipoObjetivo.HIPERTROFIA)).thenReturn(estrategia);
        when(estrategia.calcular(1780, 2759, new BigDecimal("80"))).thenReturn(metaEsperada);

        MetaNutricionalCalculadaDTO resultado =
                service.calcular(USUARIO_ID, new BigDecimal("80"), NivelAtividade.MODERADO, TipoObjetivo.HIPERTROFIA);

        // TMB = 10*80 + 6.25*180 - 5*30 + 5 = 1780; GET = round(1780 * 1.55) = 2759
        assertThat(resultado).isEqualTo(metaEsperada);
    }

    @Test
    void deveLancarBusinessExceptionQuandoPerfilEstiverIncompleto() {
        service = new CalculoNutricionalService(perfilService, estrategiaFactory);

        PerfilResponseDTO perfilIncompleto =
                new PerfilResponseDTO("Fulano", "fulano@ironfit.com", null, null, null, null, null, null);
        when(perfilService.obterPerfil(USUARIO_ID)).thenReturn(perfilIncompleto);

        assertThatThrownBy(() -> service.calcular(
                        USUARIO_ID, new BigDecimal("80"), NivelAtividade.MODERADO, TipoObjetivo.HIPERTROFIA))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(estrategiaFactory);
    }
}
