package com.ironfit.api.auth.mapper;

import com.ironfit.api.auth.dto.UsuarioResponseDTO;
import com.ironfit.api.auth.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getFotoPerfilUrl());
    }
}
