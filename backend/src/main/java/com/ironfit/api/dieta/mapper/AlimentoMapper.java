package com.ironfit.api.dieta.mapper;

import com.ironfit.api.dieta.dto.AlimentoResponseDTO;
import com.ironfit.api.dieta.entity.Alimento;
import org.springframework.stereotype.Component;

@Component
public class AlimentoMapper {

    public AlimentoResponseDTO toResponseDTO(Alimento alimento) {
        return new AlimentoResponseDTO(
                alimento.getId(),
                alimento.getNome(),
                alimento.getMarca(),
                alimento.getCategoria(),
                alimento.getCaloriasPor100g(),
                alimento.getProteinaPor100g(),
                alimento.getCarboidratoPor100g(),
                alimento.getGorduraPor100g(),
                alimento.getFibraPor100g(),
                alimento.getUsuario() != null);
    }
}
