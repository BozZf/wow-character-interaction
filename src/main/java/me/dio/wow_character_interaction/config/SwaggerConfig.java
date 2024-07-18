package me.dio.wow_character_interaction.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customSwagger() {
        return new OpenAPI()
                .info(new Info()
                        .title("WOW Characters Chat")
                        .version("V1")
                        .description("API to chat with World Of Warcraft characters.")
                        .contact(new Contact()
                                .name("Felipe Bozzoni")
                                .url("https://www.linkedin.com/in/felipe-bozzoni/")
                                .email("fe.bozzoni@gmail.com")));
    }
}
