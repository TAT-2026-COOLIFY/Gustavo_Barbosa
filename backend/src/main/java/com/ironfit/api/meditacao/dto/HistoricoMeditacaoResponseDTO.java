package com.ironfit.api.meditacao.dto;

import com.ironfit.api.meditacao.entity.TipoMeditacao;
import java.time.Instant;

public record HistoricoMeditacaoResponseDTO(
        Long id, String meditacaoNome, TipoMeditacao tipo, Instant dataRealizacao, Integer duracaoSegundos, boolean concluido) {}
