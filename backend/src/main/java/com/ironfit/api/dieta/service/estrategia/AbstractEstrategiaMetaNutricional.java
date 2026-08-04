package com.ironfit.api.dieta.service.estrategia;

import com.ironfit.api.dieta.dto.MetaNutricionalCalculadaDTO;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Base com os cálculos comuns a todas as estratégias: distribuição de macros a partir de g/kg e meta de água. */
abstract class AbstractEstrategiaMetaNutricional implements EstrategiaMetaNutricional {

    private static final BigDecimal ML_AGUA_POR_KG = BigDecimal.valueOf(35);
    private static final int KCAL_POR_GRAMA_PROTEINA = 4;
    private static final int KCAL_POR_GRAMA_GORDURA = 9;
    private static final int KCAL_POR_GRAMA_CARBOIDRATO = 4;

    /**
     * Monta a meta a partir das calorias-alvo e da quantidade de proteína/gordura por kg de peso corporal;
     * o restante das calorias é preenchido com carboidratos.
     */
    protected MetaNutricionalCalculadaDTO montarMeta(
            int tmbKcal,
            int gastoTotalDiarioKcal,
            int metaCaloriasKcal,
            BigDecimal proteinaGramasPorKg,
            BigDecimal gorduraGramasPorKg,
            BigDecimal pesoReferenciaKg) {
        int proteinaG = proteinaGramasPorKg.multiply(pesoReferenciaKg).setScale(0, RoundingMode.HALF_UP).intValue();
        int gorduraG = gorduraGramasPorKg.multiply(pesoReferenciaKg).setScale(0, RoundingMode.HALF_UP).intValue();

        int caloriasRestantes =
                metaCaloriasKcal - (proteinaG * KCAL_POR_GRAMA_PROTEINA) - (gorduraG * KCAL_POR_GRAMA_GORDURA);
        int carboidratoG = Math.max(caloriasRestantes, 0) / KCAL_POR_GRAMA_CARBOIDRATO;

        int metaAguaMl =
                pesoReferenciaKg.multiply(ML_AGUA_POR_KG).setScale(0, RoundingMode.HALF_UP).intValue();

        return new MetaNutricionalCalculadaDTO(
                tmbKcal, gastoTotalDiarioKcal, metaCaloriasKcal, proteinaG, carboidratoG, gorduraG, metaAguaMl);
    }
}
