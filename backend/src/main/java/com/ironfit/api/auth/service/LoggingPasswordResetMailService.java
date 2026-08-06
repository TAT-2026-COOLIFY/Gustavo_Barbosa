package com.ironfit.api.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

/**
 * Implementação de desenvolvimento: registra o link de redefinição no log em vez de enviar um e-mail
 * real. Ativa automaticamente quando {@code spring.mail.username} (env var {@code MAIL_USERNAME}) não
 * está configurado, evitando exigir credenciais SMTP para rodar o projeto localmente. Assim que
 * {@code MAIL_USERNAME}/{@code MAIL_PASSWORD} são definidos, {@link SmtpPasswordResetMailService} assume
 * o lugar desta (as duas nunca coexistem — condições complementares, ver anotações de cada uma).
 */
@Service
@ConditionalOnExpression("'${spring.mail.username:}'.isBlank()")
public class LoggingPasswordResetMailService implements PasswordResetMailService {

    private static final Logger log = LoggerFactory.getLogger(LoggingPasswordResetMailService.class);

    @Value("${ironfit.mail.frontend-base-url}")
    private String frontendBaseUrl;

    @Override
    public void enviarLinkRedefinicao(String destinatario, String nomeUsuario, String tokenBruto) {
        log.info(
                "[DEV] MAIL_USERNAME não configurado — link de redefinição de senha para {} ({}): {}/redefinir-senha?token={}",
                nomeUsuario,
                destinatario,
                frontendBaseUrl,
                tokenBruto);
    }
}
