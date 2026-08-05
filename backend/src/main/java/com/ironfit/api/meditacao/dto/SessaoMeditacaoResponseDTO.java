package com.ironfit.api.meditacao.dto;

import com.ironfit.api.meditacao.entity.TipoMeditacao;
import java.time.Instant;

public record SessaoMeditacaoResponseDTO(
        Long id,
        Long meditacaoId,
        String meditacaoNome,
        TipoMeditacao tipo,
        Integer duracaoMinutosSugerida,
        Integer inspirarSegundos,
        Integer segurarSegundos,
        Integer expirarSegundos,
        Instant dataRealizacao,
        Integer duracaoSegundos,
        boolean concluido) {}
