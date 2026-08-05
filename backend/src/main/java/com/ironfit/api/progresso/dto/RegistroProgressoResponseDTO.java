package com.ironfit.api.progresso.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistroProgressoResponseDTO(
        Long id,
        LocalDate dataRegistro,
        BigDecimal pesoKg,
        BigDecimal imc,
        BigDecimal percentualGordura,
        BigDecimal massaMagraKg,
        BigDecimal circunferenciaCinturaCm,
        BigDecimal circunferenciaQuadrilCm,
        BigDecimal circunferenciaBracoCm,
        BigDecimal circunferenciaCoxaCm,
        BigDecimal circunferenciaPeitoCm,
        String fotoUrl,
        String observacoes) {}
