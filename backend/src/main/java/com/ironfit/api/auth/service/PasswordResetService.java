package com.ironfit.api.auth.service;

import com.ironfit.api.auth.entity.PasswordResetToken;
import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.PasswordResetTokenRepository;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.exception.BusinessException;
import com.ironfit.api.util.TokenHasher;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetMailService passwordResetMailService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Value("${ironfit.security.password-reset-token-ttl-hours}")
    private long tokenTtlHours;

    public PasswordResetService(
            UsuarioRepository usuarioRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordResetMailService passwordResetMailService,
            RefreshTokenService refreshTokenService,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordResetMailService = passwordResetMailService;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void solicitarRedefinicao(String email) {
        usuarioRepository.findByEmailIgnoreCase(email).ifPresent(usuario -> {
            String tokenBruto = TokenHasher.gerarTokenBruto();

            PasswordResetToken token = PasswordResetToken.builder()
                    .usuario(usuario)
                    .tokenHash(TokenHasher.hash(tokenBruto))
                    .expiraEm(Instant.now().plusSeconds(tokenTtlHours * 3600))
                    .build();

            passwordResetTokenRepository.save(token);
            passwordResetMailService.enviarLinkRedefinicao(usuario.getEmail(), usuario.getNome(), tokenBruto);
        });
        // Não revela se o e-mail existe ou não — mesma resposta em ambos os casos.
    }

    @Transactional
    public void redefinir(String tokenBruto, String novaSenha) {
        PasswordResetToken token = passwordResetTokenRepository
                .findByTokenHashAndUsadoFalse(TokenHasher.hash(tokenBruto))
                .orElseThrow(() -> new BusinessException("Token de redefinição inválido ou já utilizado"));

        if (token.getExpiraEm().isBefore(Instant.now())) {
            throw new BusinessException("Token de redefinição expirado");
        }

        Usuario usuario = token.getUsuario();
        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        token.setUsado(true);
        passwordResetTokenRepository.save(token);

        refreshTokenService.revogarTodosDoUsuario(usuario.getId());
    }
}
