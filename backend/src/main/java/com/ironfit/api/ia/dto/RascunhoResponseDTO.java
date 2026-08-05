package com.ironfit.api.ia.dto;

import com.ironfit.api.ia.entity.StatusRascunhoIa;
import com.ironfit.api.ia.entity.TipoRascunhoIa;

/** {@code dados} é um {@code TreinoRequestDTO} ou {@code DietaRequestDTO}, dependendo de {@code tipo}. */
public record RascunhoResponseDTO(Long id, TipoRascunhoIa tipo, StatusRascunhoIa status, Object dados) {}
