package me.dio.wow_character_interaction.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import me.dio.wow_character_interaction.configs.UsernameEnc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PathVariableEncryptionFilter implements Filter {

    @Autowired
    private UsernameEnc usernameEnc;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String uri = httpRequest.getRequestURI();

        if (uri.contains("/auth/refresh/")) {
            String[] parts = uri.split("/auth/refresh/");
            if (parts.length == 2) {
                String username = parts[1];

                String encryptedUsername = usernameEnc.encrypt(username);

                String encryptedUri = "/api/v1/auth/refresh/" + encryptedUsername;

                HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(httpRequest) {
                    @Override
                    public String getRequestURI() {
                        return encryptedUri;
                    }
                };
                chain.doFilter(wrappedRequest, response);
            }
        } else if (uri.contains("/users/update/")) {
            String[] parts = uri.split("/users/update/");
            if (parts.length == 2) {
                String username = parts[1];

                String encryptedUsername = usernameEnc.encrypt(username);

                String encryptedUri = "/api/v1/users/update/" + encryptedUsername;

                HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(httpRequest) {
                    @Override
                    public String getRequestURI() {
                        return encryptedUri;
                    }
                };

                chain.doFilter(wrappedRequest, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
