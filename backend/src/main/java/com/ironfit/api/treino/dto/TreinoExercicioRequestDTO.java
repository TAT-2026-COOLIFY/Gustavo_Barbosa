package com.ironfit.api.treino.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TreinoExercicioRequestDTO(
        @NotNull(message = "Exercício é obrigatório") Long exercicioId,
        @NotNull @Min(1) Integer ordem,
        @NotNull(message = "Séries alvo é obrigatório") @Min(1) @Max(20) Integer seriesAlvo,
        @NotNull(message = "Repetições alvo é obrigatório") @Min(1) @Max(100) Integer repeticoesAlvo,
        BigDecimal cargaAlvoKg,
        @Min(0) Integer descansoSegundos) {}
