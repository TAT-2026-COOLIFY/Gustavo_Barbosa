package com.ironfit.api.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SmtpPasswordResetMailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private SmtpPasswordResetMailService service;

    @BeforeEach
    void setUp() {
        service = new SmtpPasswordResetMailService(mailSender);
        ReflectionTestUtils.setField(service, "remetente", "no-reply@ironfit.com");
        ReflectionTestUtils.setField(service, "frontendBaseUrl", "http://localhost:5173");
        when(mailSender.createMimeMessage())
                .thenAnswer(invocation -> new MimeMessage(jakarta.mail.Session.getInstance(new Properties())));
    }

    @Test
    void enviarLinkRedefinicao_deveMontarLinkComTokenEEnviar() throws Exception {
        service.enviarLinkRedefinicao("usuario@ironfit.com", "Fulano", "token-abc-123");

        var captor = org.mockito.ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());

        MimeMessage mensagemEnviada = captor.getValue();
        assertThat(mensagemEnviada.getSubject()).isEqualTo("IronFit — Redefinição de senha");
        assertThat(mensagemEnviada.getAllRecipients()[0].toString()).isEqualTo("usuario@ironfit.com");
        assertThat(mensagemEnviada.getFrom()[0].toString()).isEqualTo("no-reply@ironfit.com");
    }

    @Test
    void enviarLinkRedefinicao_falhaNoEnvio_naoDevePropagarExcecao() {
        doThrow(new org.springframework.mail.MailSendException("smtp indisponível"))
                .when(mailSender)
                .send(any(MimeMessage.class));

        service.enviarLinkRedefinicao("usuario@ironfit.com", "Fulano", "token-abc-123");
        // Não deve lançar — solicitarRedefinicao() nunca deve revelar falha de envio ao chamador.
    }
}
