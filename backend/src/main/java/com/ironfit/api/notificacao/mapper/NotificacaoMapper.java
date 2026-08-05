package com.ironfit.api.notificacao.mapper;

import com.ironfit.api.notificacao.dto.NotificacaoResponseDTO;
import com.ironfit.api.notificacao.entity.Notificacao;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoMapper {

    public NotificacaoResponseDTO toResponseDTO(Notificacao notificacao) {
        return new NotificacaoResponseDTO(
                notificacao.getId(),
                notificacao.getTipo(),
                notificacao.getTitulo(),
                notificacao.getMensagem(),
                notificacao.isLida(),
                notificacao.getCriadoEm());
    }
}
