package com.ironfit.api.treino.dto;

import com.ironfit.api.treino.entity.OrigemTreino;
import java.util.List;

public record TreinoResponseDTO(
        Long id, String nome, OrigemTreino origem, boolean favorito, List<TreinoExercicioResponseDTO> exercicios) {}
