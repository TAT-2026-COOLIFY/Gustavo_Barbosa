package com.ironfit.api.treino.dto;

import com.ironfit.api.treino.entity.OrigemTreino;

public record TreinoResumoDTO(
        Long id, String nome, OrigemTreino origem, boolean favorito, int quantidadeExercicios) {}
