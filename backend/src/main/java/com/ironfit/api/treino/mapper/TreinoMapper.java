package com.ironfit.api.treino.mapper;

import com.ironfit.api.treino.dto.TreinoExercicioResponseDTO;
import com.ironfit.api.treino.dto.TreinoResponseDTO;
import com.ironfit.api.treino.dto.TreinoResumoDTO;
import com.ironfit.api.treino.entity.Treino;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TreinoMapper {

    public TreinoResponseDTO toResponseDTO(Treino treino, boolean favorito, List<TreinoExercicioResponseDTO> exercicios) {
        return new TreinoResponseDTO(treino.getId(), treino.getNome(), treino.getOrigem(), favorito, exercicios);
    }

    public TreinoResumoDTO toResumoDTO(Treino treino, boolean favorito, int quantidadeExercicios) {
        return new TreinoResumoDTO(treino.getId(), treino.getNome(), treino.getOrigem(), favorito, quantidadeExercicios);
    }
}
