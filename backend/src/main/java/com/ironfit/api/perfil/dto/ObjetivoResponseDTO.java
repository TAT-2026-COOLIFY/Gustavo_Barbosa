package com.ironfit.api.perfil.dto;

import com.ironfit.api.perfil.entity.TipoObjetivo;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ObjetivoResponseDTO(
        Long id, TipoObjetivo tipo, BigDecimal pesoMetaKg, LocalDate dataInicio, LocalDate dataAlvo, boolean ativo) {}
