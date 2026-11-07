package me.dio.wow_character_interaction.service.impl;

import me.dio.wow_character_interaction.data.dto.security.AccountCredentialsDto;
import me.dio.wow_character_interaction.data.dto.security.TokenDto;
import me.dio.wow_character_interaction.security.jwt.JwtTokenProvider;
import me.dio.wow_character_interaction.service.AuthService;
import org.springframework.hateoas.Link;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public TokenDto signin(AccountCredentialsDto credentials) {
        var authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(credentials.getUsername(), credentials.getPassword()));
        var roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return addLinks(tokenProvider.createAccessToken(authentication.getName(), roles));
    }

    @Override
    public TokenDto refreshToken(String username, String refreshToken) {
        return addLinks(tokenProvider.refreshToken(username, refreshToken));
    }

    private TokenDto addLinks(TokenDto token) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/api/v1/auth";
        token.add(Link.of(baseUrl + "/signin", "Authenticates a User"));
        token.add(Link.of(baseUrl + "/refresh/:username", "Refresh token"));
        return token;
    }
}
