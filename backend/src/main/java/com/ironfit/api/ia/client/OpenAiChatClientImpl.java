package com.ironfit.api.ia.client;

import com.ironfit.api.exception.BusinessException;
import com.ironfit.api.ia.config.IaProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class OpenAiChatClientImpl implements OpenAiChatClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiChatClientImpl.class);

    /** Espera máxima (em segundos) aceita para tentar de novo sozinho depois de um 429 do provedor. */
    private static final long ESPERA_MAXIMA_RETRY_SEGUNDOS = 20;

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

        Map<String, Object> corpo = new HashMap<>();
        corpo.put("model", iaProperties.getModelo());
        corpo.put("messages", mensagens);
        corpo.put("response_format", Map.of("type", "json_object"));
        corpo.put("temperature", 0.4);
        // Modelos de raciocínio (gpt-oss) "pensam" antes de responder, e esses tokens contam no limite por
        // minuto do provedor. Para um chat de dúvidas de treino/dieta, raciocínio curto basta.
        if (iaProperties.getModelo().contains("gpt-oss")) {
            corpo.put("reasoning_effort", "low");
        }

        RespostaChatCompletions resposta;
        try {
            resposta = chamar(corpo);
        } catch (HttpClientErrorException.TooManyRequests e) {
            // Limite de uso do provedor (ex.: tokens por minuto no plano gratuito da Groq). Se o provedor pedir
            // uma espera curta, espera e tenta mais uma vez antes de devolver o erro para o usuário.
            long espera = segundosParaTentarDeNovo(e);
            if (espera > ESPERA_MAXIMA_RETRY_SEGUNDOS) {
                throw limiteAtingido();
            }
            log.info("Limite do provedor de IA atingido, tentando de novo em {}s", espera);
            try {
                Thread.sleep(espera * 1000);
                resposta = chamar(corpo);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw limiteAtingido();
            } catch (HttpClientErrorException.TooManyRequests e2) {
                throw limiteAtingido();
            } catch (RestClientException e2) {
                throw falhaGenerica(e2);
            }
        } catch (RestClientException e) {
            throw falhaGenerica(e);
        }

        if (resposta == null || resposta.choices() == null || resposta.choices().isEmpty()) {
            throw new BusinessException("Resposta vazia do assistente de IA");
        }
        if (resposta.usage() != null) {
            log.debug(
                    "IA tokens: prompt={} resposta={} total={}",
                    resposta.usage().prompt_tokens(),
                    resposta.usage().completion_tokens(),
                    resposta.usage().total_tokens());
        }

        return resposta.choices().get(0).message().content();
    }

    private RespostaChatCompletions chamar(Map<String, Object> corpo) {
        return restClient
                .post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + iaProperties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(corpo)
                .retrieve()
                .body(RespostaChatCompletions.class);
    }

    /** Lê o cabeçalho {@code retry-after} (segundos) do 429; sem ele, assume uma espera curta padrão. */
    private long segundosParaTentarDeNovo(HttpClientErrorException e) {
        String retryAfter = e.getResponseHeaders() != null ? e.getResponseHeaders().getFirst("retry-after") : null;
        if (retryAfter == null) {
            return 10;
        }
        try {
            return Math.max(1, (long) Math.ceil(Double.parseDouble(retryAfter.trim())));
        } catch (NumberFormatException ex) {
            return 10;
        }
    }

    private BusinessException limiteAtingido() {
        return new BusinessException(
                "O assistente recebeu muitas mensagens em pouco tempo. Aguarde alguns segundos e tente novamente.");
    }

    private BusinessException falhaGenerica(RestClientException e) {
        log.warn("Falha ao chamar o provedor de IA: {}", e.getMessage());
        return new BusinessException("Não foi possível falar com o assistente de IA agora. Tente novamente em instantes.");
    }

    private record RespostaChatCompletions(List<Escolha> choices, Uso usage) {}

    private record Escolha(MensagemChat message) {}

    private record Uso(Integer prompt_tokens, Integer completion_tokens, Integer total_tokens) {}
}
