package me.dio.wow_character_interaction.configs;

import me.dio.wow_character_interaction.security.jwt.JwtTokenFilter;
import me.dio.wow_character_interaction.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenProvider tokenProvider) throws Exception {
        JwtTokenFilter customFilter = new JwtTokenFilter(tokenProvider);
        return http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authorizeHttpRequests -> authorizeHttpRequests
                                .requestMatchers(
                                        "/api/v1/auth/signin",
                                        "/api/v1/auth/refresh/{username}",
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/api/v1/users/create"
                                ).permitAll()
                                .requestMatchers(
                                        "/api/v1/character/find-all",
                                        "/api/v1/character/find/{id}",
                                        "/api/v1/users/update/{username}/full-name",
                                        "/api/v1/users/update/{username}/password",
                                        "/api/v1/character/ask/{id}"
                                ).authenticated()
                                .requestMatchers(
                                        "/api/v1/character/create",
                                        "/api/v1/character/update/{id}",
                                        "/api/v1/character/delete/{id}"
                                ).hasAnyAuthority("ADMIN", "MANAGER")
                                .anyRequest().denyAll()
                )
                .build();
    }
}
