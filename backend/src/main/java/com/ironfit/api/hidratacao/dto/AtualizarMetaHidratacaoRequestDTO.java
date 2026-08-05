package com.ironfit.api.hidratacao.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AtualizarMetaHidratacaoRequestDTO(
        @NotNull(message = "Meta é obrigatória")
                @Min(value = 500, message = "Meta deve ser ao menos 500ml")
                @Max(value = 10000, message = "Meta deve ser no máximo 10000ml")
                Integer metaMl) {}
