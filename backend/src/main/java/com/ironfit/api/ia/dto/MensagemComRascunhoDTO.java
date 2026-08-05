package com.ironfit.api.ia.dto;

import com.ironfit.api.ia.entity.PapelMensagemIa;
import java.time.Instant;

public record MensagemComRascunhoDTO(
        Long id, PapelMensagemIa papel, String conteudo, Instant criadoEm, RascunhoResponseDTO rascunho) {}
