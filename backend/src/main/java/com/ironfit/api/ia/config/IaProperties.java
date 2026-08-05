package com.ironfit.api.ia.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Configuração do provedor de IA (OpenAI). A chave nunca é hardcoded — vem de variável de ambiente. */
@Configuration
@ConfigurationProperties(prefix = "ironfit.ia")
public class IaProperties {

    private String apiKey;
    private String baseUrl = "https://api.openai.com/v1";
    private String modelo = "gpt-4o-mini";

    public boolean isConfigurada() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
}
