package com.ironfit.api.auth.service;

import com.ironfit.api.auth.entity.RefreshToken;
import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.RefreshTokenRepository;
import com.ironfit.api.exception.UnauthorizedException;
import com.ironfit.api.security.JwtProperties;
import com.ironfit.api.util.TokenHasher;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public String gerar(Usuario usuario) {
        String tokenBruto = TokenHasher.gerarTokenBruto();

        RefreshToken refreshToken = RefreshToken.builder()
                .usuario(usuario)
                .tokenHash(TokenHasher.hash(tokenBruto))
                .expiraEm(Instant.now().plus(jwtProperties.getRefreshTokenTtlDays(), ChronoUnit.DAYS))
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenBruto;
    }

    @Transactional
    public RotacaoResultado validarERotacionar(String tokenBruto) {
        RefreshToken tokenAtual = refreshTokenRepository
                .findByTokenHashAndRevogadoFalse(TokenHasher.hash(tokenBruto))
                .orElseThrow(() -> new UnauthorizedException("Refresh token inválido"));

        if (tokenAtual.getExpiraEm().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expirado");
        }

        tokenAtual.setRevogado(true);
        refreshTokenRepository.save(tokenAtual);

        String novoTokenBruto = gerar(tokenAtual.getUsuario());
        return new RotacaoResultado(tokenAtual.getUsuario(), novoTokenBruto);
    }

    @Transactional
    public void revogar(String tokenBruto) {
        refreshTokenRepository.findByTokenHashAndRevogadoFalse(TokenHasher.hash(tokenBruto)).ifPresent(token -> {
            token.setRevogado(true);
            refreshTokenRepository.save(token);
        });
    }

    @Transactional
    public void revogarTodosDoUsuario(Long usuarioId) {
        refreshTokenRepository.revogarTodosDoUsuario(usuarioId);
    }

    public record RotacaoResultado(Usuario usuario, String novoRefreshToken) {}
}
