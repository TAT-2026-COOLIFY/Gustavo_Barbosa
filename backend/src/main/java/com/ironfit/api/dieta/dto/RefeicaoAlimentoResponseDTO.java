package com.ironfit.api.dieta.dto;

import java.math.BigDecimal;

public record RefeicaoAlimentoResponseDTO(
        Long id,
        Long alimentoId,
        String alimentoNome,
        BigDecimal quantidadeGramas,
        BigDecimal calorias,
        BigDecimal proteinaG,
        BigDecimal carboidratoG,
        BigDecimal gorduraG) {}
