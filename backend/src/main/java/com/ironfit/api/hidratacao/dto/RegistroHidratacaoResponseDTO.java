package com.ironfit.api.hidratacao.dto;

import java.time.Instant;

public record RegistroHidratacaoResponseDTO(Long id, Integer quantidadeMl, Instant registradoEm) {}
