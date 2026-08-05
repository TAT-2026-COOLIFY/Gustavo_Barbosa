package com.ironfit.api.meditacao.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ConcluirMeditacaoRequestDTO(@NotNull(message = "Duração é obrigatória") @Min(0) Integer duracaoSegundos) {}
