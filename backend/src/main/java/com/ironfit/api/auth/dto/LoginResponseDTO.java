package com.ironfit.api.auth.dto;

public record LoginResponseDTO(
        String accessToken, String refreshToken, long expiresInSeconds, UsuarioResponseDTO usuario) {}
