package com.ironfit.api.ia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EnviarMensagemRequestDTO(
        Long conversaId, @NotBlank(message = "Mensagem é obrigatória") @Size(max = 2000) String mensagem) {}
