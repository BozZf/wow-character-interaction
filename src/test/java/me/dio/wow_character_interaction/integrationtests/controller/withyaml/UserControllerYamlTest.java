package me.dio.wow_character_interaction.integrationtests.controller.withyaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import me.dio.wow_character_interaction.configs.TestsConfig;
import me.dio.wow_character_interaction.integrationtests.dto.TestAccountCredentialsDto;
import me.dio.wow_character_interaction.integrationtests.dto.TestTokenDto;
import me.dio.wow_character_interaction.integrationtests.dto.TestUserDto;
import me.dio.wow_character_interaction.integrationtests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.*;
import me.dio.wow_character_interaction.adapter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@Tag("yaml")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerYamlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YAMLMapper objectMapper;

    private static final String USERNAME = "test-" + UUID.randomUUID();

    private static TestUserDto userDto;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public static void setup() {
        objectMapper = new YAMLMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        userDto = new TestUserDto();
    }

    @Test
    @Order(0)
    public void createUser() throws IOException {
        mockUser();

        var content = given()
                        .basePath("/api/v1/users")
                        .config(RestAssured
                                .config()
                                .encoderConfig(EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(TestsConfig.CONTENT_TYPE_YML, ContentType.TEXT)))
                        .port(TestsConfig.SERVER_PORT)
                        .contentType(TestsConfig.CONTENT_TYPE_YML)
                        .accept(TestsConfig.CONTENT_TYPE_YML)
                            .body(objectMapper.writeValueAsString(userDto))
                        .when()
                            .post("/create")
                        .then()
                            .statusCode(200)
                        .extract()
                            .body()
                                .asString();

        TestUserDto createdUser = objectMapper.readValue(content, TestUserDto.class);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getUsername());
        assertNotNull(createdUser.getFullName());
        assertNull(createdUser.getPassword());

        assertEquals(USERNAME, createdUser.getUsername());
        assertEquals("Test Full Name", createdUser.getFullName());
        assertTrue(passwordEncoder.matches("TestPassword123", userRepository.findByUsername(USERNAME).getPassword()));
    }

    @Test
    @Order(1)
    public void authorization() throws JsonMappingException, JsonProcessingException {
        TestAccountCredentialsDto credentialsDto = new TestAccountCredentialsDto(USERNAME,
                                                "TestPassword123");

        var content = given()
                        .config(RestAssured
                                .config()
                                .encoderConfig(EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(TestsConfig.CONTENT_TYPE_YML, ContentType.TEXT)))
                        .basePath("/api/v1/auth")
                        .port(TestsConfig.SERVER_PORT)
                        .contentType(TestsConfig.CONTENT_TYPE_YML)
                        .accept(TestsConfig.CONTENT_TYPE_YML)
                            .body(objectMapper.writeValueAsString(credentialsDto))
                        .when()
                            .post("/signin")
                        .then()
                            .statusCode(200)
                        .extract()
                            .body()
                                .asString();

        String token = objectMapper.readValue(content, TestTokenDto.class).getAccessToken();

        specification = new RequestSpecBuilder()
                            .addHeader(TestsConfig.HEADER_PARAM_AUTHORIZATION, "Bearer " + token )
                            .setBasePath("/api/v1/users")
                            .setPort(TestsConfig.SERVER_PORT)
                            .build();
    }

    @Test
    @Order(2)
    public void updateUserFullName() throws IOException {

        var content = given()
                        .spec(specification)
                        .config(RestAssured
                                .config()
                                .encoderConfig(EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(TestsConfig.CONTENT_TYPE_YML, ContentType.TEXT)))
                        .pathParam("username", USERNAME)
                        .contentType(TestsConfig.CONTENT_TYPE_YML)
                        .accept(TestsConfig.CONTENT_TYPE_YML)
                            .body("Test Updated Full Name")
                        .when()
                            .patch("/update/{username}/full-name")
                        .then()
                            .statusCode(200)
                        .extract()
                            .body()
                                .asString();

        TestUserDto updatedUser = objectMapper.readValue(content, TestUserDto.class);

        assertNotNull(updatedUser);
        assertNotNull(updatedUser.getUsername());
        assertNotNull(updatedUser.getFullName());
        assertNull(updatedUser.getPassword());

        assertEquals(USERNAME, updatedUser.getUsername());
        assertEquals("Test Updated Full Name", updatedUser.getFullName());
    }

    @Test
    @Order(3)
    public void updateUserPassword() throws IOException {

        var content = given()
                        .spec(specification)
                        .config(RestAssured
                                .config()
                                .encoderConfig(EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(TestsConfig.CONTENT_TYPE_YML, ContentType.TEXT)))
                        .pathParam("username", USERNAME)
                        .contentType(TestsConfig.CONTENT_TYPE_YML)
                        .accept(TestsConfig.CONTENT_TYPE_YML)
                            .body("TestUpdatedPassword123")
                        .when()
                            .patch("/update/{username}/password")
                        .then()
                            .statusCode(200)
                        .extract()
                            .body()
                                .asString();

        TestUserDto updatedUser = objectMapper.readValue(content, TestUserDto.class);

        assertNotNull(updatedUser);
        assertNotNull(updatedUser.getUsername());
        assertNotNull(updatedUser.getFullName());
        assertNull(updatedUser.getPassword());

        assertEquals(USERNAME, updatedUser.getUsername());
        assertTrue(passwordEncoder.matches("TestUpdatedPassword123", userRepository.findByUsername(USERNAME).getPassword()));
    }

    public void mockUser() {
        userDto.setUsername(USERNAME);
        userDto.setFullName("Test Full Name");
        userDto.setPassword("TestPassword123");
    }
}
