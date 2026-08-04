package com.ironfit.api.dieta.service.estrategia;

import static org.assertj.core.api.Assertions.assertThat;

import com.ironfit.api.dieta.dto.MetaNutricionalCalculadaDTO;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ManutencaoEstrategiaTest {

    private final ManutencaoEstrategia estrategia = new ManutencaoEstrategia();

    @Test
    void deveSuportarManutencaoCondicionamentoESaude() {
        assertThat(estrategia.tiposSuportados())
                .containsExactlyInAnyOrder(TipoObjetivo.MANUTENCAO, TipoObjetivo.CONDICIONAMENTO, TipoObjetivo.SAUDE);
    }

    @Test
    void deveManterCaloriasIguaisAoGastoTotal() {
        MetaNutricionalCalculadaDTO meta = estrategia.calcular(1500, 2200, new BigDecimal("70"));

        assertThat(meta.metaCaloriasKcal()).isEqualTo(2200);
        assertThat(meta.metaProteinaG()).isEqualTo(126); // 1.8g/kg * 70kg
        assertThat(meta.metaGorduraG()).isEqualTo(70); // 1.0g/kg * 70kg
        assertThat(meta.metaCarboidratoG()).isEqualTo(266); // (2200 - 504 - 630) / 4
        assertThat(meta.metaAguaMl()).isEqualTo(2450); // 35ml/kg * 70kg
    }
}
