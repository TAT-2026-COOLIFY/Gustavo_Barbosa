package com.ironfit.api.dieta.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;

public record DiarioRefeicaoDTO(
        Long refeicaoId,
        String nome,
        LocalTime horarioSugerido,
        BigDecimal calorias,
        BigDecimal proteinaG,
        BigDecimal carboidratoG,
        BigDecimal gorduraG,
        boolean consumida,
        Instant consumidoEm) {}
