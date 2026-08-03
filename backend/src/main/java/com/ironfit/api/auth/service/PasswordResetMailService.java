package com.ironfit.api.auth.service;

/**
 * Porta de envio de e-mail de redefinição de senha (Strategy). A implementação real via
 * SMTP é plugável depois sem alterar {@link PasswordResetService}.
 */
public interface PasswordResetMailService {

    void enviarLinkRedefinicao(String destinatario, String nomeUsuario, String tokenBruto);
}
