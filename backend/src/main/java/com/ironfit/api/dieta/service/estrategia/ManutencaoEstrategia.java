package com.ironfit.api.dieta.service.estrategia;

import com.ironfit.api.dieta.dto.MetaNutricionalCalculadaDTO;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import java.math.BigDecimal;
import java.util.Set;
import org.springframework.stereotype.Component;

/** Estratégia para MANUTENCAO/CONDICIONAMENTO/SAUDE: calorias iguais ao gasto total, com macros balanceados. */
@Component
public class ManutencaoEstrategia extends AbstractEstrategiaMetaNutricional {

    private static final double FATOR_MANUTENCAO = 1.0;
    private static final BigDecimal PROTEINA_G_POR_KG = BigDecimal.valueOf(1.8);
    private static final BigDecimal GORDURA_G_POR_KG = BigDecimal.valueOf(1.0);

    @Override
    public Set<TipoObjetivo> tiposSuportados() {
        return Set.of(TipoObjetivo.MANUTENCAO, TipoObjetivo.CONDICIONAMENTO, TipoObjetivo.SAUDE);
    }

    @Override
    public MetaNutricionalCalculadaDTO calcular(int tmbKcal, int gastoTotalDiarioKcal, BigDecimal pesoReferenciaKg) {
        int metaCalorias = (int) Math.round(gastoTotalDiarioKcal * FATOR_MANUTENCAO);
        return montarMeta(tmbKcal, gastoTotalDiarioKcal, metaCalorias, PROTEINA_G_POR_KG, GORDURA_G_POR_KG, pesoReferenciaKg);
    }
}
