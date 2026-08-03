package com.ironfit.api.treino.dto;

import java.math.BigDecimal;

public record TreinoExercicioResponseDTO(
        Long id,
        ExercicioResponseDTO exercicio,
        Integer ordem,
        Integer seriesAlvo,
        Integer repeticoesAlvo,
        BigDecimal cargaAlvoKg,
        Integer descansoSegundos) {}
