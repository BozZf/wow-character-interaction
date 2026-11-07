package me.dio.wow_character_interaction.configs;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class GeminiApiConfig {

    @Bean
    public RequestInterceptor apiKeyRequestInterceptor(@Value("${gemini.api.key}") String apiKey) {
        return requestTemplate -> requestTemplate.header("x-goog-api-key", apiKey);
    }
}
