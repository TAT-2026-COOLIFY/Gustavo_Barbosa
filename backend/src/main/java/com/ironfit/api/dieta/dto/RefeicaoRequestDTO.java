package com.ironfit.api.dieta.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import java.util.List;

public record RefeicaoRequestDTO(
        @NotBlank(message = "Nome é obrigatório") @Size(max = 80) String nome,
        @NotNull @Min(1) Integer ordem,
        LocalTime horarioSugerido,
        @NotEmpty(message = "Refeição deve ter ao menos um alimento") @Valid List<RefeicaoAlimentoRequestDTO> alimentos) {}
