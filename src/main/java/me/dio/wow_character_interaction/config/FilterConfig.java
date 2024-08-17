package me.dio.wow_character_interaction.config;

import me.dio.wow_character_interaction.security.PathVariableEncryptionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<PathVariableEncryptionFilter> loggingFilter(){
        FilterRegistrationBean<PathVariableEncryptionFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new PathVariableEncryptionFilter());
        registrationBean.addUrlPatterns(
                "/api/v1/auth/refresh/**",
                "/api/v1/users/update/**");

        return registrationBean;
    }
}
