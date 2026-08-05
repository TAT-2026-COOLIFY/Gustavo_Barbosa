package com.ironfit.api.ia.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * O Spring Boot 4 usa Jackson 3 ({@code tools.jackson.databind.ObjectMapper}) para o bean padrão do
 * framework. O módulo de IA precisa especificamente do Jackson 2 clássico ({@code com.fasterxml.jackson}),
 * já transitivamente presente no classpath (springdoc/jjwt), para trabalhar com {@code JsonNode} ao
 * interpretar a resposta do modelo — este bean isola essa necessidade sem interferir no Jackson do resto da app.
 */
@Configuration
public class IaJacksonConfig {

    @Bean
    public ObjectMapper iaObjectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
