package com.ironfit.api.ia.dto;

import java.util.List;

public record ConversaDetalheDTO(Long id, String titulo, List<MensagemComRascunhoDTO> mensagens) {}
