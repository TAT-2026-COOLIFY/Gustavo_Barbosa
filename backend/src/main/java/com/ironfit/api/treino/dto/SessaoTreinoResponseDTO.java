package com.ironfit.api.treino.dto;

import java.time.Instant;
import java.util.List;

public record SessaoTreinoResponseDTO(
        Long id,
        Long treinoId,
        String treinoNome,
        Instant dataExecucao,
        Integer duracaoSegundos,
        boolean concluido,
        List<TreinoExercicioResponseDTO> exercicios) {}
