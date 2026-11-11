package com.ironfit.api.ia.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Formato interno (contrato de prompt) esperado como conteúdo bruto retornado pelo modelo. Nunca exposto pela API.
 * {@code foraDoEscopo} é a autoclassificação do modelo: quando true, o backend descarta o texto gerado e responde
 * com a mensagem padrão de fora do escopo.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RespostaBrutaIaDTO(String resposta, SugestaoBrutaDTO sugestao, Boolean foraDoEscopo) {

    public RespostaBrutaIaDTO(String resposta, SugestaoBrutaDTO sugestao) {
        this(resposta, sugestao, null);
    }
}
