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
        // Layout em tabela (não flex/grid) de propósito: é o padrão que sobrevive ao motor de
        // renderização do Outlook desktop (Word), que ignora a maior parte de CSS moderno.
        // Cores/raio replicam os tokens do tema do site (index.css: --primary, --radius=0.75rem)
        // e o gradiente roxo->azul do favicon.svg; fonte de marca é a mesma Poppins usada no header
        // do DashboardPage, com fallback igual ao do site quando o cliente de e-mail não carrega a fonte.
        return """
                <!doctype html>
                <html lang="pt-BR">
                  <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <title>IronFit</title>
                  </head>
                  <body style="margin:0; padding:0; background-color:#f1f0fb; font-family:'Segoe UI', Arial, sans-serif;">
                    <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f1f0fb; padding:32px 16px;">
                      <tr>
                        <td align="center">
                          <table role="presentation" width="480" cellpadding="0" cellspacing="0" style="width:480px; max-width:100%%; background-color:#ffffff; border-radius:16px; overflow:hidden; box-shadow:0 4px 24px rgba(76,63,240,0.12);">
                            <tr>
                              <td style="background-color:#4c3ff0; background-image:linear-gradient(135deg, #7e14ff 0%%, #4c3ff0 55%%, #47bfff 100%%); padding:28px 32px;">
                                <table role="presentation" cellpadding="0" cellspacing="0">
                                  <tr>
                                    <td style="vertical-align:middle; padding-right:10px;">
                                      <div style="width:32px; height:32px; background-color:rgba(255,255,255,0.18); border-radius:9px; text-align:center; line-height:32px;">
                                        <span style="font-size:18px;">⚡</span>
                                      </div>
                                    </td>
                                    <td style="vertical-align:middle;">
                                      <span style="font-family:'Poppins','Segoe UI',Arial,sans-serif; font-weight:800; font-size:20px; color:#ffffff; letter-spacing:0.2px;">IronFit</span>
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:32px;">
                                <h1 style="margin:0 0 4px; font-size:18px; color:#1a1a2e; font-family:'Segoe UI',Arial,sans-serif;">Olá, %s!</h1>
                                <p style="margin:16px 0 0; font-size:15px; line-height:1.6; color:#3a3a4a;">
                                  Recebemos uma solicitação para redefinir sua senha. Clique no botão abaixo para criar uma nova senha
                                  (o link é válido por <strong>1 hora</strong>):
                                </p>
                                <table role="presentation" cellpadding="0" cellspacing="0" style="margin:28px auto;">
                                  <tr>
                                    <td align="center" style="border-radius:12px; background-color:#4c3ff0;">
                                      <a href="%s" style="display:inline-block; padding:13px 32px; font-size:15px; font-weight:600; color:#ffffff; text-decoration:none; border-radius:12px;">
                                        Redefinir minha senha
                                      </a>
                                    </td>
                                  </tr>
                                </table>
                                <p style="margin:0; font-size:13px; line-height:1.6; color:#6b6b7d;">
                                  Se o botão não funcionar, copie e cole este link no navegador:<br>
                                  <a href="%s" style="color:#4c3ff0; word-break:break-all;">%s</a>
                                </p>
                                <hr style="border:none; border-top:1px solid #ececf5; margin:24px 0;">
                                <p style="margin:0; font-size:12px; line-height:1.6; color:#9a9aab;">
                                  Se você não solicitou essa alteração, pode ignorar este e-mail — sua senha continua a mesma.
                                </p>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:16px 32px; background-color:#f7f7fc; text-align:center;">
                                <span style="font-size:11px; color:#a3a3b5;">IronFit — Gestão de Dietas, Treinos e Bem-Estar</span>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """
                .formatted(nomeUsuario, link, link, link);
    }
}
