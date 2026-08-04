package com.ironfit.api.dieta.dto;

import com.ironfit.api.dieta.entity.NivelAtividade;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CalcularMetasRequestDTO(
        @NotNull(message = "Peso de referência é obrigatório")
                @DecimalMin(value = "20.0", message = "Peso deve ser ao menos 20kg")
                @DecimalMax(value = "400.0", message = "Peso deve ser no máximo 400kg")
                BigDecimal pesoReferenciaKg,
        @NotNull(message = "Nível de atividade é obrigatório") NivelAtividade nivelAtividade,
        @NotNull(message = "Objetivo é obrigatório") TipoObjetivo tipoObjetivo) {}
