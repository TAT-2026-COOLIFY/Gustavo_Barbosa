package com.ironfit.api.treino.dto;

import java.time.Instant;

public record HistoricoSessaoResponseDTO(
        Long id, String treinoNome, Instant dataExecucao, Integer duracaoSegundos, boolean concluido, int totalSeries) {}
