package com.ironfit.api.treino.dto;

import java.math.BigDecimal;

public record SerieExecutadaResponseDTO(
        Long id, Long treinoExercicioId, Integer numeroSerie, Integer repeticoesRealizadas, BigDecimal cargaRealizadaKg) {}
