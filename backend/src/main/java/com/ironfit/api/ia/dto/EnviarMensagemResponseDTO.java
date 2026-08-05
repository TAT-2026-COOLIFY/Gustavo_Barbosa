package com.ironfit.api.ia.dto;

public record EnviarMensagemResponseDTO(
        Long conversaId,
        MensagemResponseDTO mensagemUsuario,
        MensagemResponseDTO mensagemAssistente,
        RascunhoResponseDTO rascunho) {}
