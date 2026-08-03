package com.ironfit.api.perfil.mapper;

import com.ironfit.api.perfil.dto.ObjetivoResponseDTO;
import com.ironfit.api.perfil.entity.Objetivo;
import org.springframework.stereotype.Component;

@Component
public class ObjetivoMapper {

    public ObjetivoResponseDTO toResponseDTO(Objetivo objetivo) {
        return new ObjetivoResponseDTO(
                objetivo.getId(),
                objetivo.getTipo(),
                objetivo.getPesoMetaKg(),
                objetivo.getDataInicio(),
                objetivo.getDataAlvo(),
                objetivo.isAtivo());
    }
}
