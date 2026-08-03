package com.ironfit.api.treino.dto;

import com.ironfit.api.treino.entity.GrupoMuscular;

public record ExercicioResponseDTO(
        Long id, String nome, GrupoMuscular grupoMuscular, String descricao, String equipamento) {}
