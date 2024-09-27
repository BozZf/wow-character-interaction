package me.dio.wow_character_interaction.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import me.dio.wow_character_interaction.configs.UsernameEnc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PathVariableEncryptionFilter implements Filter {

    Logger logger = LoggerFactory.getLogger(PathVariableEncryptionFilter.class);

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
            String[] parts = uri.split("/");
            if (parts[6].equals("full-name") || parts[6].equals("password")) {
                String username = parts[5];

                String encryptedUsername = usernameEnc.encrypt(username);

                String encryptedUri = "/api/v1/users/update/" + encryptedUsername + "/" + parts[6];

                HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(httpRequest) {
                    @Override
                    public String getRequestURI() {
                        return encryptedUri;
                    }

                    @Override
                    public StringBuffer getRequestURL() {
                        StringBuffer originalURL = httpRequest.getRequestURL();
                        String baseURL = originalURL.substring(0, originalURL.indexOf(httpRequest.getRequestURI()));
                        return new StringBuffer(baseURL + encryptedUri);
                    }

                    @Override
                    public String getContextPath() {
                        return httpRequest.getContextPath();
                    }
                };
                logger.info("Url Username: " + wrappedRequest.getRequestURL());
                chain.doFilter(wrappedRequest, response);
            } else {
                logger.info("Uri ID: " + ((HttpServletRequest) request).getRequestURI());
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
