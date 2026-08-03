package com.ironfit.api.perfil.dto;

import com.ironfit.api.perfil.entity.NivelExperiencia;
import com.ironfit.api.perfil.entity.Sexo;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AtualizarPerfilRequestDTO(
        @NotBlank(message = "Nome é obrigatório") @Size(max = 120) String nome,
        @Past(message = "Data de nascimento deve estar no passado") LocalDate dataNascimento,
        Sexo sexo,
        @DecimalMin(value = "50.0", message = "Altura deve ser ao menos 50cm")
                @DecimalMax(value = "260.0", message = "Altura deve ser no máximo 260cm")
                BigDecimal alturaCm,
        NivelExperiencia nivelExperiencia,
        @Size(max = 500, message = "Bio deve ter no máximo 500 caracteres") String bio) {}
