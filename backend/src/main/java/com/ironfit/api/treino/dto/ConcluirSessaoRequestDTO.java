package com.ironfit.api.treino.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ConcluirSessaoRequestDTO(@NotNull(message = "Duração é obrigatória") @Min(0) Integer duracaoSegundos) {}
