package com.ironfit.api.perfil.dto;

import com.ironfit.api.perfil.entity.TipoObjetivo;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CriarObjetivoRequestDTO(
        @NotNull(message = "Tipo de objetivo é obrigatório") TipoObjetivo tipo,
        @DecimalMin(value = "20.0", message = "Peso meta deve ser ao menos 20kg")
                @DecimalMax(value = "400.0", message = "Peso meta deve ser no máximo 400kg")
                BigDecimal pesoMetaKg,
        LocalDate dataAlvo) {}
