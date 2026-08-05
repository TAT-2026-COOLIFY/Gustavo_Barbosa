package com.ironfit.api.meditacao.dto;

import com.ironfit.api.meditacao.entity.TipoMeditacao;

public record MeditacaoResponseDTO(
        Long id,
        String nome,
        TipoMeditacao tipo,
        Integer duracaoMinutos,
        String descricao,
        String instrucoes,
        Integer inspirarSegundos,
        Integer segurarSegundos,
        Integer expirarSegundos) {}
