package com.ironfit.api.dieta.dto;

public record MetaNutricionalCalculadaDTO(
        int tmbKcal,
        int gastoTotalDiarioKcal,
        int metaCaloriasKcal,
        int metaProteinaG,
        int metaCarboidratoG,
        int metaGorduraG,
        int metaAguaMl) {}
