package com.ironfit.api.ia.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.ironfit.api.ia.entity.TipoRascunhoIa;

/** Formato interno esperado dentro do campo {@code sugestao} da resposta JSON do modelo. Nunca exposto pela API. */
public record SugestaoBrutaDTO(TipoRascunhoIa tipo, JsonNode dados) {}
