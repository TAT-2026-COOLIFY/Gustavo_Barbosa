package com.ironfit.api.dieta.service.estrategia;

import com.ironfit.api.dieta.dto.MetaNutricionalCalculadaDTO;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import java.math.BigDecimal;
import java.util.Set;
import org.springframework.stereotype.Component;

/** Estratégia para HIPERTROFIA: superávit calórico de 12% sobre o gasto total, com proteína e gordura elevadas. */
@Component
public class SuperavitCaloricoEstrategia extends AbstractEstrategiaMetaNutricional {

    private static final double FATOR_SUPERAVIT = 1.12;
    private static final BigDecimal PROTEINA_G_POR_KG = BigDecimal.valueOf(2.0);
    private static final BigDecimal GORDURA_G_POR_KG = BigDecimal.valueOf(1.0);

    @Override
    public Set<TipoObjetivo> tiposSuportados() {
        return Set.of(TipoObjetivo.HIPERTROFIA);
    }

    @Override
    public MetaNutricionalCalculadaDTO calcular(int tmbKcal, int gastoTotalDiarioKcal, BigDecimal pesoReferenciaKg) {
        int metaCalorias = (int) Math.round(gastoTotalDiarioKcal * FATOR_SUPERAVIT);
        return montarMeta(tmbKcal, gastoTotalDiarioKcal, metaCalorias, PROTEINA_G_POR_KG, GORDURA_G_POR_KG, pesoReferenciaKg);
    }
}
