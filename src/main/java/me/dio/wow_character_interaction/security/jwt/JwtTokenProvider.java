package me.dio.wow_character_interaction.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import me.dio.wow_character_interaction.adapter.controller.exception.InvalidJwtAuthenticationException;
import me.dio.wow_character_interaction.data.dto.security.TokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenProvider {

    private static final String TOKEN_USE = "token_use";
    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";
    private static final String BEARER_PREFIX = "Bearer ";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long validityInMilliseconds;
    private final UserDetailsService userDetailsService;
    private final AccountStatusUserDetailsChecker accountStatusChecker = new AccountStatusUserDetailsChecker();

    public JwtTokenProvider(@Value("${jwt.secret.key}") String secretKey,
                            @Value("${jwt.token.expire-length}") long validityInMilliseconds,
                            UserDetailsService userDetailsService) {
        this.algorithm = Algorithm.HMAC256(Base64.getEncoder().encode(secretKey.getBytes(StandardCharsets.UTF_8)));
        this.verifier = JWT.require(algorithm).withClaimPresence("exp").withClaimPresence("sub").build();
        this.validityInMilliseconds = validityInMilliseconds;
        this.userDetailsService = userDetailsService;
    }

    public TokenDto createAccessToken(String username, List<String> roles) {
        Date now = new Date();
        Date accessExpiration = new Date(now.getTime() + validityInMilliseconds);
        Date refreshExpiration = new Date(now.getTime() + validityInMilliseconds * 3);
        String issuer = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        String accessToken = JWT.create()
                .withClaim(TOKEN_USE, ACCESS)
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(accessExpiration)
                .withSubject(username)
                .withIssuer(issuer)
                .sign(algorithm);
        String refreshToken = JWT.create()
                .withClaim(TOKEN_USE, REFRESH)
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(refreshExpiration)
                .withSubject(username)
                .sign(algorithm);

        return new TokenDto(username, true, now, accessExpiration, accessToken, refreshToken);
    }

    public TokenDto refreshToken(String username, String authorization) {
        DecodedJWT token = verify(authorization, REFRESH);
        if (!username.equals(token.getSubject())) {
            throw new InvalidJwtAuthenticationException("Token belongs to another user");
        }

        UserDetails user = loadActiveUser(username);
        List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return createAccessToken(username, roles);
    }

    public Authentication getAuthentication(String authorization) {
        DecodedJWT token = verify(authorization, ACCESS);
        UserDetails user = loadActiveUser(token.getSubject());
        return UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
    }

    private UserDetails loadActiveUser(String username) {
        UserDetails user = userDetailsService.loadUserByUsername(username);
        accountStatusChecker.check(user);
        return user;
    }

    private DecodedJWT verify(String authorization, String expectedUse) {
        String value = authorization.startsWith(BEARER_PREFIX)
                ? authorization.substring(BEARER_PREFIX.length()) : authorization;
        try {
            DecodedJWT token = verifier.verify(value);
            String actualUse = token.getClaim(TOKEN_USE).asString();
            if (token.getClaim(TOKEN_USE).isMissing()) {
                // Older access tokens included an issuer, while refresh tokens omitted it.
                actualUse = token.getIssuer() == null ? REFRESH : ACCESS;
            }
            if (!expectedUse.equals(actualUse) || token.getSubject() == null || token.getSubject().isBlank()
                    || token.getExpiresAt() == null) {
                throw new InvalidJwtAuthenticationException("Unexpected token type or missing claims");
            }
            return token;
        } catch (JWTVerificationException e) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT");
        }
    }
}
