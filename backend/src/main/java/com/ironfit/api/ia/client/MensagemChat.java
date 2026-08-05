package com.ironfit.api.ia.client;

/** Uma mensagem no formato da API de chat completions da OpenAI ({@code role}/{@code content}). */
public record MensagemChat(String role, String content) {

    public static MensagemChat sistema(String content) {
        return new MensagemChat("system", content);
    }

    public static MensagemChat usuario(String content) {
        return new MensagemChat("user", content);
    }

    public static MensagemChat assistente(String content) {
        return new MensagemChat("assistant", content);
    }
}
