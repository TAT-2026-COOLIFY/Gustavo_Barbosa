package com.ironfit.api.hidratacao.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RegistroHidratacaoRequestDTO(
        @NotNull(message = "Quantidade é obrigatória")
                @Min(value = 1, message = "Quantidade deve ser maior que zero")
                @Max(value = 5000, message = "Quantidade deve ser no máximo 5000ml")
                Integer quantidadeMl) {}
