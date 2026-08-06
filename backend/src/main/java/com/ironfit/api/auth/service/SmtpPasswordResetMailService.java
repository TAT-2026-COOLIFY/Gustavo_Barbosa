package com.ironfit.api.auth.service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Envia de verdade o e-mail de redefinição de senha via SMTP (Spring Mail + {@link JavaMailSender},
 * autoconfigurado a partir de {@code spring.mail.*}). Ativa automaticamente quando
 * {@code spring.mail.username} (env var {@code MAIL_USERNAME}) está configurado — ver
 * {@link LoggingPasswordResetMailService} para o comportamento sem credenciais.
 */
@Service
@ConditionalOnExpression("!'${spring.mail.username:}'.isBlank()")
public class SmtpPasswordResetMailService implements PasswordResetMailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpPasswordResetMailService.class);

    private final JavaMailSender mailSender;

    @Value("${ironfit.mail.from}")
    private String remetente;

    @Value("${ironfit.mail.frontend-base-url}")
    private String frontendBaseUrl;

    public SmtpPasswordResetMailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostConstruct
    void logAtivacao() {
        // Confirma no startup, sem ambiguidade, qual das duas implementações da Strategy está no ar
        // (fácil de checar sem precisar adivinhar se MAIL_USERNAME foi realmente lido pelo processo).
        log.info("[MAIL] Envio real de e-mail ATIVO (SmtpPasswordResetMailService) — remetente configurado via MAIL_FROM/MAIL_USERNAME");
    }

    @Override
    public void enviarLinkRedefinicao(String destinatario, String nomeUsuario, String tokenBruto) {
        String link = frontendBaseUrl + "/redefinir-senha?token=" + tokenBruto;

        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            // multipart=true: obrigatório para setText(plain, html) registrar as duas partes como alternativas.
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");
            helper.setFrom(remetente);
            helper.setTo(destinatario);
            helper.setSubject("IronFit — Redefinição de senha");
            helper.setText(textoTextoSimples(nomeUsuario, link), corpoHtml(nomeUsuario, link));

            mailSender.send(mensagem);
            log.info("[MAIL] E-mail de redefinição de senha enviado com sucesso para {}", destinatario);
        } catch (MessagingException | MailException e) {
            // Não relança: solicitarRedefinicao() nunca deve revelar ao chamador se o envio falhou
            // (mesma resposta para e-mail existente ou não). O erro fica registrado para diagnóstico.
            log.error("[MAIL] Falha ao enviar e-mail de redefinição de senha para {}", destinatario, e);
        }
    }

    private String textoTextoSimples(String nomeUsuario, String link) {
        return "Olá, " + nomeUsuario + "!\n\n"
                + "Recebemos uma solicitação para redefinir sua senha no IronFit.\n"
                + "Acesse o link abaixo para criar uma nova senha (válido por 1 hora):\n\n"
                + link + "\n\n"
                + "Se você não solicitou isso, ignore este e-mail — sua senha continua a mesma.";
    }

    private String corpoHtml(String nomeUsuario, String link) {
        return """
                <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 480px; margin: 0 auto; padding: 32px 24px; color: #1a1a2e;">
                  <h1 style="font-size: 20px; margin-bottom: 4px;">IronFit</h1>
                  <p style="font-size: 15px; line-height: 1.5;">Olá, %s!</p>
                  <p style="font-size: 15px; line-height: 1.5;">
                    Recebemos uma solicitação para redefinir sua senha. Clique no botão abaixo para criar uma nova senha
                    (o link é válido por 1 hora):
                  </p>
                  <p style="text-align: center; margin: 28px 0;">
                    <a href="%s" style="background: #4c3ff0; color: #ffffff; padding: 12px 28px; border-radius: 8px; text-decoration: none; font-weight: 600; display: inline-block;">
                      Redefinir minha senha
                    </a>
                  </p>
                  <p style="font-size: 13px; line-height: 1.5; color: #666;">
                    Se o botão não funcionar, copie e cole este link no navegador:<br>
                    <a href="%s" style="color: #4c3ff0;">%s</a>
                  </p>
                  <p style="font-size: 13px; line-height: 1.5; color: #666; margin-top: 24px;">
                    Se você não solicitou essa alteração, pode ignorar este e-mail — sua senha continua a mesma.
                  </p>
                </div>
                """
                .formatted(nomeUsuario, link, link, link);
    }
}
