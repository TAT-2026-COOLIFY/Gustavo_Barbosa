package com.ironfit.api.treino.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record TreinoRequestDTO(
        @NotBlank(message = "Nome é obrigatório") @Size(max = 120) String nome,
        @NotEmpty(message = "Treino deve ter ao menos um exercício") @Valid List<TreinoExercicioRequestDTO> exercicios) {}
