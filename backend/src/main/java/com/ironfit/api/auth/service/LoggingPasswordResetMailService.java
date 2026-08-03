package com.ironfit.api.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementação de desenvolvimento: registra o link de redefinição no log em vez de
 * enviar um e-mail real, evitando exigir credenciais SMTP para rodar o projeto localmente.
 */
@Service
public class LoggingPasswordResetMailService implements PasswordResetMailService {

    private static final Logger log = LoggerFactory.getLogger(LoggingPasswordResetMailService.class);

    @Override
    public void enviarLinkRedefinicao(String destinatario, String nomeUsuario, String tokenBruto) {
        log.info(
                "[DEV] Link de redefinição de senha para {} ({}): http://localhost:5173/redefinir-senha?token={}",
                nomeUsuario,
                destinatario,
                tokenBruto);
    }
}
