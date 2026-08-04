package com.ironfit.api.dieta.dto;

import com.ironfit.api.dieta.entity.CategoriaAlimento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record AlimentoRequestDTO(
        @NotBlank(message = "Nome é obrigatório") @Size(max = 150) String nome,
        @Size(max = 100) String marca,
        @NotNull(message = "Categoria é obrigatória") CategoriaAlimento categoria,
        @NotNull(message = "Calorias por 100g é obrigatório") @DecimalMin(value = "0.0", message = "Deve ser positivo")
                BigDecimal caloriasPor100g,
        @NotNull(message = "Proteína por 100g é obrigatório") @DecimalMin(value = "0.0", message = "Deve ser positivo")
                BigDecimal proteinaPor100g,
        @NotNull(message = "Carboidrato por 100g é obrigatório") @DecimalMin(value = "0.0", message = "Deve ser positivo")
                BigDecimal carboidratoPor100g,
        @NotNull(message = "Gordura por 100g é obrigatório") @DecimalMin(value = "0.0", message = "Deve ser positivo")
                BigDecimal gorduraPor100g,
        @DecimalMin(value = "0.0", message = "Deve ser positivo") BigDecimal fibraPor100g) {}
