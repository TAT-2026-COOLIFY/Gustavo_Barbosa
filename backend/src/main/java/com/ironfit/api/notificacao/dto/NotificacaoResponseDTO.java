package com.ironfit.api.notificacao.dto;

import com.ironfit.api.notificacao.entity.TipoNotificacao;
import java.time.Instant;

public record NotificacaoResponseDTO(
        Long id, TipoNotificacao tipo, String titulo, String mensagem, boolean lida, Instant criadoEm) {}
