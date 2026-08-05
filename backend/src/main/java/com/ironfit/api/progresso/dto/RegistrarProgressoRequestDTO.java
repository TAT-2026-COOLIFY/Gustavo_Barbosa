package com.ironfit.api.progresso.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistrarProgressoRequestDTO(
        LocalDate dataRegistro,
        @NotNull(message = "Peso é obrigatório")
                @DecimalMin(value = "20.0", message = "Peso deve ser ao menos 20kg")
                @DecimalMax(value = "400.0", message = "Peso deve ser no máximo 400kg")
                BigDecimal pesoKg,
        @DecimalMin(value = "0.0", message = "% de gordura não pode ser negativo")
                @DecimalMax(value = "75.0", message = "% de gordura deve ser no máximo 75%")
                BigDecimal percentualGordura,
        BigDecimal circunferenciaCinturaCm,
        BigDecimal circunferenciaQuadrilCm,
        BigDecimal circunferenciaBracoCm,
        BigDecimal circunferenciaCoxaCm,
        BigDecimal circunferenciaPeitoCm,
        String observacoes) {}
