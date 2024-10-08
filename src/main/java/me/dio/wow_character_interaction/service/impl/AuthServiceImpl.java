package me.dio.wow_character_interaction.service.impl;

import me.dio.wow_character_interaction.adapter.repository.UserRepository;
import me.dio.wow_character_interaction.data.dto.security.AccountCredentialsDto;
import me.dio.wow_character_interaction.data.dto.security.TokenDto;
import me.dio.wow_character_interaction.security.jwt.JwtTokenProvider;
import me.dio.wow_character_interaction.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @SuppressWarnings("rawtypes")
    @Override
    public ResponseEntity signin(AccountCredentialsDto accountCredentialsDto) {
        try {
            var username = accountCredentialsDto.getUsername();
            var password = accountCredentialsDto.getPassword();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            var user = userRepository.findByUsername(username);

            var tokenResponse = new TokenDto();
            if (user != null) {
                tokenResponse = tokenProvider.createAccessToken(username, user.getRoles());
                addLinks(tokenResponse);
            } else {
                throw new UsernameNotFoundException("Username " + username + " not found!");
            }
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username/password!");
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ResponseEntity refreshToken(String username, String refreshToken) {
        var user = userRepository.findByUsername(username);

        var tokenResponse = new TokenDto();
        if (user != null) {
            tokenResponse = tokenProvider.refreshToken(refreshToken);
            addLinks(tokenResponse);
        } else {
            throw new UsernameNotFoundException("Username " + username + " not found!");
        }
        return ResponseEntity.ok(tokenResponse);
    }

    private void addLinks(TokenDto dto) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .build()
                .toUriString() + "/api/v1/auth";

        URI signInUri = UriComponentsBuilder
                .fromUriString(baseUrl + "/signin")
                .build()
                .toUri();
        Link signInLink = Link.of(signInUri.toString(), "Authenticates a User");
        dto.add(signInLink);

        URI refreshUri = UriComponentsBuilder
                .fromUriString(baseUrl + "/refresh/:username")
                .build()
                .toUri();
        Link refreshLink = Link.of(refreshUri.toString(), "Refresh token");
        dto.add(refreshLink);
    }
}
