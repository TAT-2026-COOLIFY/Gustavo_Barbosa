package com.ironfit.api.dieta.service.estrategia;

import static org.assertj.core.api.Assertions.assertThat;

import com.ironfit.api.dieta.dto.MetaNutricionalCalculadaDTO;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class DeficitCaloricoEstrategiaTest {

    private final DeficitCaloricoEstrategia estrategia = new DeficitCaloricoEstrategia();

    @Test
    void deveSuportarApenasEmagrecimento() {
        assertThat(estrategia.tiposSuportados()).containsExactly(TipoObjetivo.EMAGRECIMENTO);
    }

    @Test
    void deveAplicarDeficitDe20PorCentoEProteinaAlta() {
        MetaNutricionalCalculadaDTO meta = estrategia.calcular(1500, 2000, new BigDecimal("80"));

        assertThat(meta.tmbKcal()).isEqualTo(1500);
        assertThat(meta.gastoTotalDiarioKcal()).isEqualTo(2000);
        assertThat(meta.metaCaloriasKcal()).isEqualTo(1600); // 2000 * 0.8
        assertThat(meta.metaProteinaG()).isEqualTo(176); // 2.2g/kg * 80kg
        assertThat(meta.metaGorduraG()).isEqualTo(64); // 0.8g/kg * 80kg
        assertThat(meta.metaCarboidratoG()).isEqualTo(80); // (1600 - 704 - 576) / 4
        assertThat(meta.metaAguaMl()).isEqualTo(2800); // 35ml/kg * 80kg
    }
}
