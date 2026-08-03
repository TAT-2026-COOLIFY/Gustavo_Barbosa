package com.ironfit.api.perfil.dto;

import com.ironfit.api.perfil.entity.NivelExperiencia;
import com.ironfit.api.perfil.entity.Sexo;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PerfilResponseDTO(
        String nome,
        String email,
        String fotoPerfilUrl,
        LocalDate dataNascimento,
        Sexo sexo,
        BigDecimal alturaCm,
        NivelExperiencia nivelExperiencia,
        String bio) {}
