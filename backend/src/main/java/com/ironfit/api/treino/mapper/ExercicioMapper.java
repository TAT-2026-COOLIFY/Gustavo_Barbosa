package com.ironfit.api.treino.mapper;

import com.ironfit.api.treino.dto.ExercicioResponseDTO;
import com.ironfit.api.treino.entity.Exercicio;
import org.springframework.stereotype.Component;

@Component
public class ExercicioMapper {

    public ExercicioResponseDTO toResponseDTO(Exercicio exercicio) {
        return new ExercicioResponseDTO(
                exercicio.getId(),
                exercicio.getNome(),
                exercicio.getGrupoMuscular(),
                exercicio.getDescricao(),
                exercicio.getEquipamento());
    }
}
