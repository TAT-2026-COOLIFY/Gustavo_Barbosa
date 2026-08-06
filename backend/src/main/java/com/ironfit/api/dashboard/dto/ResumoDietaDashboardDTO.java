package com.ironfit.api.dashboard.dto;

import java.math.BigDecimal;

public record ResumoDietaDashboardDTO(
        boolean temDietaAtiva,
        String dietaNome,
        Integer metaCaloriasKcal,
        BigDecimal caloriasConsumidas,
        Integer metaProteinaG,
        BigDecimal proteinaConsumidaG,
        Integer metaCarboidratoG,
        BigDecimal carboidratoConsumidoG,
        Integer metaGorduraG,
        BigDecimal gorduraConsumidaG) {}
