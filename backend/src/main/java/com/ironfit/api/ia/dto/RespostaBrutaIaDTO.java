package com.ironfit.api.ia.dto;

/** Formato interno (contrato de prompt) esperado como conteúdo bruto retornado pelo modelo. Nunca exposto pela API. */
public record RespostaBrutaIaDTO(String resposta, SugestaoBrutaDTO sugestao) {}
