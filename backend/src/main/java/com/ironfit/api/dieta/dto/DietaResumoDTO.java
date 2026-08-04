package com.ironfit.api.dieta.dto;

import com.ironfit.api.dieta.entity.OrigemDieta;

public record DietaResumoDTO(
        Long id,
        String nome,
        OrigemDieta origem,
        boolean ativa,
        boolean favorito,
        Integer metaCaloriasKcal,
        Integer metaProteinaG,
        Integer metaCarboidratoG,
        Integer metaGorduraG,
        int quantidadeRefeicoes) {}
