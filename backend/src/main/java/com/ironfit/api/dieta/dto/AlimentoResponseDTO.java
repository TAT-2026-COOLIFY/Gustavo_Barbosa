package com.ironfit.api.dieta.dto;

import com.ironfit.api.dieta.entity.CategoriaAlimento;
import java.math.BigDecimal;

public record AlimentoResponseDTO(
        Long id,
        String nome,
        String marca,
        CategoriaAlimento categoria,
        BigDecimal caloriasPor100g,
        BigDecimal proteinaPor100g,
        BigDecimal carboidratoPor100g,
        BigDecimal gorduraPor100g,
        BigDecimal fibraPor100g,
        boolean personalizado) {}
