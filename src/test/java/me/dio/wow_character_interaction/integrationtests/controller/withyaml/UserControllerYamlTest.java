package me.dio.wow_character_interaction.integrationtests.controller.withyaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import me.dio.wow_character_interaction.configs.TestsConfig;
import me.dio.wow_character_interaction.integrationtests.dto.TestAccountCredentialsDto;
import me.dio.wow_character_interaction.integrationtests.dto.TestTokenDto;
import me.dio.wow_character_interaction.integrationtests.dto.TestUserDto;
import me.dio.wow_character_interaction.integrationtests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@Tag("yaml")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(
        initializers = AbstractIntegrationTest.Initializer.class
)
@TestPropertySource("classpath:application-test.properties")
public class UserControllerYamlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YAMLMapper objectMapper;

    private static TestUserDto userDto;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        assertNotNull(createdUser.getPassword());

        assertEquals("TestName", createdUser.getUsername());
        assertEquals("Test Full Name", createdUser.getFullName());
        assertTrue(passwordEncoder.matches("TestPassword123", createdUser.getPassword()));
    }

    @Test
    @Order(1)
    public void authorization() throws JsonMappingException, JsonProcessingException {
        TestAccountCredentialsDto credentialsDto = new TestAccountCredentialsDto("TestName",
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

        String accessToken = objectMapper.readValue(content, TestTokenDto.class).getAccessToken();

        specification = new RequestSpecBuilder()
                            .addHeader(TestsConfig.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken )
                            .setBasePath("/api/v1/users")
                            .setPort(TestsConfig.SERVER_PORT)
                                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                            .build();
    }

    @Test
    @Order(2)
    public void updateUser() throws IOException {
        updateMockedUser();

        var content = given()
                        .spec(specification)
                        .config(RestAssured
                                .config()
                                .encoderConfig(EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(TestsConfig.CONTENT_TYPE_YML, ContentType.TEXT)))
                        .pathParam("username", "TestName")
                        .contentType(TestsConfig.CONTENT_TYPE_YML)
                        .accept(TestsConfig.CONTENT_TYPE_YML)
                            .body(objectMapper.writeValueAsString(userDto))
                        .when()
                            .put("/update/{username}")
                        .then()
                            .statusCode(200)
                        .extract()
                            .body()
                                .asString();

        TestUserDto updatedUser = objectMapper.readValue(content, TestUserDto.class);

        assertNotNull(updatedUser);
        assertNotNull(updatedUser.getUsername());
        assertNotNull(updatedUser.getFullName());
        assertNotNull(updatedUser.getPassword());

        assertEquals("TestName", updatedUser.getUsername());
        assertEquals("Test Updated Full Name", updatedUser.getFullName());
        assertTrue(passwordEncoder.matches("TestUpdatedPassword123", updatedUser.getPassword()));
    }

    public void mockUser() {
        userDto.setUsername("TestName");
        userDto.setFullName("Test Full Name");
        userDto.setPassword("TestPassword123");
    }

    public void updateMockedUser() {
        userDto.setFullName("Test Updated Full Name");
        userDto.setPassword("TestUpdatedPassword123");
    }
}
