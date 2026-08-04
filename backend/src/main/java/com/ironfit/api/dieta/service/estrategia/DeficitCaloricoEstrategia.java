package com.ironfit.api.dieta.service.estrategia;

import com.ironfit.api.dieta.dto.MetaNutricionalCalculadaDTO;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import java.math.BigDecimal;
import java.util.Set;
import org.springframework.stereotype.Component;

/** Estratégia para EMAGRECIMENTO: déficit calórico de 20% sobre o gasto total, com proteína alta para preservar massa magra. */
@Component
public class DeficitCaloricoEstrategia extends AbstractEstrategiaMetaNutricional {

    private static final double FATOR_DEFICIT = 0.8;
    private static final BigDecimal PROTEINA_G_POR_KG = BigDecimal.valueOf(2.2);
    private static final BigDecimal GORDURA_G_POR_KG = BigDecimal.valueOf(0.8);

    @Override
    public Set<TipoObjetivo> tiposSuportados() {
        return Set.of(TipoObjetivo.EMAGRECIMENTO);
    }

    @Override
    public MetaNutricionalCalculadaDTO calcular(int tmbKcal, int gastoTotalDiarioKcal, BigDecimal pesoReferenciaKg) {
        int metaCalorias = (int) Math.round(gastoTotalDiarioKcal * FATOR_DEFICIT);
        return montarMeta(tmbKcal, gastoTotalDiarioKcal, metaCalorias, PROTEINA_G_POR_KG, GORDURA_G_POR_KG, pesoReferenciaKg);
    }
}
