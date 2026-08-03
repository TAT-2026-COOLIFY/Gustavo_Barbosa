package com.ironfit.api.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Tokens opacos (refresh token, reset de senha) não precisam de hash lento como BCrypt —
 * são aleatórios de alta entropia, não senhas escolhidas por humanos. SHA-256 é suficiente
 * e permite lookup por igualdade de hash no banco.
 */
public final class TokenHasher {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private TokenHasher() {}

    public static String gerarTokenBruto() {
        byte[] bytes = new byte[48];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String hash(String tokenBruto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(tokenBruto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 não disponível", ex);
        }
    }
}
