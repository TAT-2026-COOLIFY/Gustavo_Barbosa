package com.ironfit.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaRequestDTO(
        @NotBlank(message = "Token é obrigatório") String token,
        @NotBlank(message = "Nova senha é obrigatória")
                @Size(min = 8, max = 72, message = "Senha deve ter entre 8 e 72 caracteres")
                @Pattern(
                        regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
                        message = "Senha deve conter ao menos uma letra e um número")
                String novaSenha) {}
