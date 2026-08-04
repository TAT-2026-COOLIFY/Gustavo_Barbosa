package com.ironfit.api.dieta.service.estrategia;

import static org.assertj.core.api.Assertions.assertThat;

import com.ironfit.api.dieta.dto.MetaNutricionalCalculadaDTO;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SuperavitCaloricoEstrategiaTest {

    private final SuperavitCaloricoEstrategia estrategia = new SuperavitCaloricoEstrategia();

    @Test
    void deveSuportarApenasHipertrofia() {
        assertThat(estrategia.tiposSuportados()).containsExactly(TipoObjetivo.HIPERTROFIA);
    }

    @Test
    void deveAplicarSuperavitDe12PorCento() {
        MetaNutricionalCalculadaDTO meta = estrategia.calcular(1600, 2500, new BigDecimal("80"));

        assertThat(meta.metaCaloriasKcal()).isEqualTo(2800); // 2500 * 1.12
        assertThat(meta.metaProteinaG()).isEqualTo(160); // 2.0g/kg * 80kg
        assertThat(meta.metaGorduraG()).isEqualTo(80); // 1.0g/kg * 80kg
        assertThat(meta.metaCarboidratoG()).isEqualTo(360); // (2800 - 640 - 720) / 4
        assertThat(meta.metaAguaMl()).isEqualTo(2800);
    }
}
