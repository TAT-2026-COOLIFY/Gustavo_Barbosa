package com.ironfit.api.perfil.mapper;

import com.ironfit.api.perfil.dto.PerfilResponseDTO;
import com.ironfit.api.perfil.entity.Perfil;
import org.springframework.stereotype.Component;

@Component
public class PerfilMapper {

    public PerfilResponseDTO toResponseDTO(Perfil perfil) {
        return new PerfilResponseDTO(
                perfil.getUsuario().getNome(),
                perfil.getUsuario().getEmail(),
                perfil.getUsuario().getFotoPerfilUrl(),
                perfil.getDataNascimento(),
                perfil.getSexo(),
                perfil.getAlturaCm(),
                perfil.getNivelExperiencia(),
                perfil.getBio());
    }
}
