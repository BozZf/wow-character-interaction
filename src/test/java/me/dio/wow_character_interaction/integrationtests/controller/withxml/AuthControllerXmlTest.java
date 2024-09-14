package me.dio.wow_character_interaction.integrationtests.controller.withxml;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import me.dio.wow_character_interaction.configs.TestsConfig;
import me.dio.wow_character_interaction.integrationtests.dto.TestAccountCredentialsDto;
import me.dio.wow_character_interaction.integrationtests.dto.TestTokenDto;
import me.dio.wow_character_interaction.integrationtests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(
        initializers = AbstractIntegrationTest.Initializer.class
)
@TestPropertySource("classpath:application-test.properties")
public class AuthControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static XmlMapper objectMapper;
    private static Date now;

    private Algorithm algorithm;
    private JWTVerifier verifier;

    @Value("${jwt.secret.key}")
    private String secretKey;

    @BeforeAll
    public static void setup() {
        objectMapper = new XmlMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        now = new Date();
    }

    @Test
    @Order(0)
    public void signin() throws IOException {
        TestAccountCredentialsDto credentialsDto = new TestAccountCredentialsDto("TestAdmin",
                                                "TestAdmin123");

        var content = given()
                        .basePath("/api/v1/auth")
                        .port(TestsConfig.SERVER_PORT)
                        .contentType(TestsConfig.CONTENT_TYPE_XML)
                        .accept(TestsConfig.CONTENT_TYPE_XML)
                            .body(objectMapper.writeValueAsString(credentialsDto))
                        .when()
                            .post("/signin")
                        .then()
                            .statusCode(200)
                        .extract()
                            .body()
                                .asString();

        TestTokenDto testDto = objectMapper.readValue(content, TestTokenDto.class);

        assertNotNull(testDto.getUsername());
        assertNotNull(testDto.getAccessToken());
        assertNotNull(testDto.getRefreshToken());

        // Access Token

        String[] jwtParts = testDto.getAccessToken().split("\\.");
        assertEquals(3, jwtParts.length);

        DecodedJWT decodedJWT = JWT.decode(testDto.getAccessToken());
        assertEquals("TestAdmin", decodedJWT.getClaim("sub").asString());
        assertEquals("HS256", decodedJWT.getHeaderClaim("alg").asString());

        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        assertTrue(roles.contains("ADMIN"));
        assertTrue(decodedJWT.getExpiresAt().after(new Date(now.getTime() + 18000000)));

        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        algorithm = Algorithm.HMAC256(secretKey.getBytes());
        verifier = JWT.require(algorithm).build();
        assertDoesNotThrow(() -> verifier.verify(testDto.getAccessToken()));

        // Refresh Token

        jwtParts = testDto.getRefreshToken().split("\\.");
        assertEquals(3, jwtParts.length);

        decodedJWT = JWT.decode(testDto.getRefreshToken());
        assertEquals("TestAdmin", decodedJWT.getClaim("sub").asString());
        assertEquals("HS256", decodedJWT.getHeaderClaim("alg").asString());

        roles = decodedJWT.getClaim("roles").asList(String.class);
        assertTrue(roles.contains("ADMIN"));
        assertTrue(decodedJWT.getExpiresAt().after(new Date(now.getTime() + (18000000 * 3))));

        assertDoesNotThrow(() -> verifier.verify(testDto.getRefreshToken()));

        specification = new RequestSpecBuilder()
                                .addHeader(TestsConfig.HEADER_PARAM_AUTHORIZATION,
                                        "Bearer " + testDto.getRefreshToken() )
                                .setBasePath("/api/v1/auth")
                                .setPort(TestsConfig.SERVER_PORT)
                                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                                .build();
    }

    @Test
    @Order(1)
    public void refresh() throws IOException {

        var content = given()
                        .spec(specification)
                        .pathParam("username", "TestAdmin")
                        .contentType(TestsConfig.CONTENT_TYPE_XML)
                        .accept(TestsConfig.CONTENT_TYPE_XML)
                        .when()
                            .put("/refresh/{username}")
                        .then()
                            .statusCode(200)
                        .extract()
                            .body()
                                .asString();

        TestTokenDto testDto = objectMapper.readValue(content, TestTokenDto.class);

        assertNotNull(testDto.getUsername());
        assertNotNull(testDto.getAccessToken());
        assertNotNull(testDto.getRefreshToken());

        // Access Token

        String[] jwtParts = testDto.getAccessToken().split("\\.");
        assertEquals(3, jwtParts.length);

        DecodedJWT decodedJWT = JWT.decode(testDto.getAccessToken());
        assertEquals("TestAdmin", decodedJWT.getClaim("sub").asString());
        assertEquals("HS256", decodedJWT.getHeaderClaim("alg").asString());

        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        assertTrue(roles.contains("ADMIN"));
        assertTrue(decodedJWT.getExpiresAt().after(new Date(now.getTime() + 18000000)));

        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        algorithm = Algorithm.HMAC256(secretKey.getBytes());
        verifier = JWT.require(algorithm).build();
        assertDoesNotThrow(() -> verifier.verify(testDto.getAccessToken()));
    }
}
