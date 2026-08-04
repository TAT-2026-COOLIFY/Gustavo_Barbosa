package com.ironfit.api.dieta.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RefeicaoAlimentoRequestDTO(
        @NotNull(message = "Alimento é obrigatório") Long alimentoId,
        @NotNull @Min(1) Integer ordem,
        @NotNull(message = "Quantidade é obrigatória") @DecimalMin(value = "0.1", message = "Quantidade deve ser positiva")
                BigDecimal quantidadeGramas) {}
