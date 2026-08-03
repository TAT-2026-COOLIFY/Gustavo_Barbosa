package com.ironfit.api.treino.service;

import com.ironfit.api.treino.dto.ExercicioResponseDTO;
import com.ironfit.api.treino.entity.GrupoMuscular;
import com.ironfit.api.treino.mapper.ExercicioMapper;
import com.ironfit.api.treino.repository.ExercicioRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ExercicioService {

    private final ExercicioRepository exercicioRepository;
    private final ExercicioMapper exercicioMapper;

    public ExercicioService(ExercicioRepository exercicioRepository, ExercicioMapper exercicioMapper) {
        this.exercicioRepository = exercicioRepository;
        this.exercicioMapper = exercicioMapper;
    }

    public List<ExercicioResponseDTO> listar(GrupoMuscular grupoMuscular) {
        var exercicios = grupoMuscular == null
                ? exercicioRepository.findAllByOrderByNome()
                : exercicioRepository.findByGrupoMuscularOrderByNome(grupoMuscular);

        return exercicios.stream().map(exercicioMapper::toResponseDTO).toList();
    }
}
