package com.ironfit.api.treino.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RegistrarSerieRequestDTO(
        @NotNull(message = "Exercício do treino é obrigatório") Long treinoExercicioId,
        @NotNull @Min(1) Integer numeroSerie,
        @NotNull(message = "Repetições realizadas é obrigatório") @Min(0) @Max(100) Integer repeticoesRealizadas,
        BigDecimal cargaRealizadaKg) {}
