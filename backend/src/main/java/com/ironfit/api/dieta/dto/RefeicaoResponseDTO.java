package com.ironfit.api.dieta.dto;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public record RefeicaoResponseDTO(
        Long id,
        String nome,
        Integer ordem,
        LocalTime horarioSugerido,
        BigDecimal caloriasTotal,
        BigDecimal proteinaTotalG,
        BigDecimal carboidratoTotalG,
        BigDecimal gorduraTotalG,
        List<RefeicaoAlimentoResponseDTO> alimentos) {}
