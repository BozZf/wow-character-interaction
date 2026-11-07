package me.dio.wow_character_interaction.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import me.dio.wow_character_interaction.adapter.controller.exception.InvalidJwtAuthenticationException;
import me.dio.wow_character_interaction.data.dto.security.TokenDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenProviderTest {

    private static final String TEST_KEY = "fake-jwt-key-for-unit-tests";
    private static final String USERNAME = "test-user";

    private final UserDetailsService users = mock(UserDetailsService.class);
    private final JwtTokenProvider provider = new JwtTokenProvider(TEST_KEY, 60_000, users);

    @BeforeEach
    void bindRequest() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void clearRequest() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void accessTokenUsesCurrentPermissions() {
        when(users.loadUserByUsername(USERNAME)).thenReturn(User.withUsername(USERNAME)
                .password("unused").authorities("MANAGER").build());
        TokenDto tokens = provider.createAccessToken(USERNAME, List.of("ADMIN"));

        var authentication = provider.getAuthentication("Bearer " + tokens.getAccessToken());

        assertEquals(USERNAME, authentication.getName());
        assertEquals(List.of("MANAGER"), authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList());
    }

    @Test
    void rejectsTokensUsedForTheWrongPurposeOrAccount() {
        TokenDto tokens = provider.createAccessToken(USERNAME, List.of("ADMIN"));

        assertThrows(InvalidJwtAuthenticationException.class,
                () -> provider.getAuthentication(tokens.getRefreshToken()));
        assertThrows(InvalidJwtAuthenticationException.class,
                () -> provider.refreshToken(USERNAME, tokens.getAccessToken()));
        assertThrows(InvalidJwtAuthenticationException.class,
                () -> provider.refreshToken("another-user", tokens.getRefreshToken()));
        verifyNoInteractions(users);
    }

    @Test
    void rejectsInvalidExpiredAndMissingExpiryClaims() {
        String expired = JWT.create().withSubject(USERNAME).withClaim("token_use", "access")
                .withExpiresAt(Date.from(Instant.now().minusSeconds(60))).sign(legacyAlgorithm());
        String missingExpiration = JWT.create().withSubject(USERNAME).withClaim("token_use", "access")
                .sign(legacyAlgorithm());

        assertThrows(InvalidJwtAuthenticationException.class, () -> provider.getAuthentication("Bearer invalid"));
        assertThrows(InvalidJwtAuthenticationException.class, () -> provider.getAuthentication(expired));
        assertThrows(InvalidJwtAuthenticationException.class, () -> provider.getAuthentication(missingExpiration));
        verifyNoInteractions(users);
    }

    @Test
    void handlesPreviouslyIssuedAccessAndRefreshTokens() {
        when(users.loadUserByUsername(USERNAME)).thenReturn(User.withUsername(USERNAME)
                .password("unused").authorities("MANAGER").build());
        Date expiration = Date.from(Instant.now().plusSeconds(60));
        String access = JWT.create().withSubject(USERNAME).withExpiresAt(expiration)
                .withIssuer("http://localhost").sign(legacyAlgorithm());
        String refresh = JWT.create().withSubject(USERNAME).withExpiresAt(expiration)
                .withClaim("roles", List.of("ADMIN")).sign(legacyAlgorithm());

        assertEquals(USERNAME, provider.getAuthentication(access).getName());
        TokenDto renewed = provider.refreshToken(USERNAME, "Bearer " + refresh);
        assertEquals(List.of("MANAGER"), JWT.decode(renewed.getAccessToken()).getClaim("roles").asList(String.class));
        assertThrows(InvalidJwtAuthenticationException.class, () -> provider.getAuthentication(refresh));
        assertThrows(InvalidJwtAuthenticationException.class, () -> provider.refreshToken(USERNAME, access));
    }

    @Test
    void disabledUsersCannotAuthenticateOrRefresh() {
        when(users.loadUserByUsername(USERNAME)).thenReturn(User.withUsername(USERNAME)
                .password("unused").authorities("ADMIN").disabled(true).build());
        TokenDto tokens = provider.createAccessToken(USERNAME, List.of("ADMIN"));

        assertThrows(DisabledException.class, () -> provider.getAuthentication(tokens.getAccessToken()));
        assertThrows(DisabledException.class, () -> provider.refreshToken(USERNAME, tokens.getRefreshToken()));
    }

    private Algorithm legacyAlgorithm() {
        return Algorithm.HMAC256(Base64.getEncoder().encode(TEST_KEY.getBytes(StandardCharsets.UTF_8)));
    }
}
