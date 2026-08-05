package com.ironfit.api.ia.dto;

import java.time.Instant;

public record ConversaResumoDTO(Long id, String titulo, Instant criadoEm, Instant atualizadoEm) {}
