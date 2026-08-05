package com.ironfit.api.ia.client;

import java.util.List;

/**
 * Abstração do provedor de IA (Strategy) — hoje implementada para a API de chat completions da
 * OpenAI; uma implementação para outro provedor pode substituí-la sem alterar {@code IaChatService}.
 */
public interface OpenAiChatClient {

    /** Envia o histórico completo da conversa (system + mensagens anteriores) e retorna o conteúdo bruto da resposta. */
    String enviarConversa(List<MensagemChat> mensagens);
}
