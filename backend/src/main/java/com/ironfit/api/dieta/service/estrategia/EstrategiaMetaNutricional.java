package com.ironfit.api.dieta.service.estrategia;

import com.ironfit.api.dieta.dto.MetaNutricionalCalculadaDTO;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Strategy: traduz o gasto energético total (TDEE) em metas de calorias e macronutrientes,
 * de acordo com o objetivo do usuário. Cada implementação sabe calcular apenas para os
 * {@link TipoObjetivo} retornados por {@link #tiposSuportados()}.
 */
public interface EstrategiaMetaNutricional {

    Set<TipoObjetivo> tiposSuportados();

    MetaNutricionalCalculadaDTO calcular(int tmbKcal, int gastoTotalDiarioKcal, BigDecimal pesoReferenciaKg);
}
