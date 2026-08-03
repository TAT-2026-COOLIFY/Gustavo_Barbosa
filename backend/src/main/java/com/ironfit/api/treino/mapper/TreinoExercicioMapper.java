package com.ironfit.api.treino.mapper;

import com.ironfit.api.treino.dto.TreinoExercicioResponseDTO;
import com.ironfit.api.treino.entity.TreinoExercicio;
import org.springframework.stereotype.Component;

@Component
public class TreinoExercicioMapper {

    private final ExercicioMapper exercicioMapper;

    public TreinoExercicioMapper(ExercicioMapper exercicioMapper) {
        this.exercicioMapper = exercicioMapper;
    }

    public TreinoExercicioResponseDTO toResponseDTO(TreinoExercicio treinoExercicio) {
        return new TreinoExercicioResponseDTO(
                treinoExercicio.getId(),
                exercicioMapper.toResponseDTO(treinoExercicio.getExercicio()),
                treinoExercicio.getOrdem(),
                treinoExercicio.getSeriesAlvo(),
                treinoExercicio.getRepeticoesAlvo(),
                treinoExercicio.getCargaAlvoKg(),
                treinoExercicio.getDescansoSegundos());
    }
}
