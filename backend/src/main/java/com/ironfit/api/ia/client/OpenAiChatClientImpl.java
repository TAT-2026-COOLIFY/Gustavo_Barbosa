package com.ironfit.api.ia.client;

import com.ironfit.api.exception.BusinessException;
import com.ironfit.api.ia.config.IaProperties;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenAiChatClientImpl implements OpenAiChatClient {

    private final RestClient restClient;
    private final IaProperties iaProperties;

    public OpenAiChatClientImpl(IaProperties iaProperties) {
        this.iaProperties = iaProperties;
        this.restClient = RestClient.builder().baseUrl(iaProperties.getBaseUrl()).build();
    }

    @Override
    public String enviarConversa(List<MensagemChat> mensagens) {
        if (!iaProperties.isConfigurada()) {
            throw new BusinessException(
                    "Assistente de IA não está configurado. Defina a variável de ambiente OPENAI_API_KEY.");
        }

        Map<String, Object> corpo = Map.of(
                "model", iaProperties.getModelo(),
                "messages", mensagens,
                "response_format", Map.of("type", "json_object"),
                "temperature", 0.4);

        RespostaChatCompletions resposta = restClient
                .post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + iaProperties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(corpo)
                .retrieve()
                .body(RespostaChatCompletions.class);

        if (resposta == null || resposta.choices() == null || resposta.choices().isEmpty()) {
            throw new BusinessException("Resposta vazia do assistente de IA");
        }

        return resposta.choices().get(0).message().content();
    }

    private record RespostaChatCompletions(List<Escolha> choices) {}

    private record Escolha(MensagemChat message) {}
}
