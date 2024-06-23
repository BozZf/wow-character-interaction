package me.dio.wow_character_interaction.adapter.client;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiApiConfig {

    @Bean
    public RequestInterceptor apiKeyRequestInterceptor(@Value("${gemini.api.key}") String apiKey) {
        return requestTemplate -> requestTemplate.query("key", apiKey);
    }
}
