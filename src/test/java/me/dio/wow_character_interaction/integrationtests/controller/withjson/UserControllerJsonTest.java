package me.dio.wow_character_interaction.integrationtests.controller.withjson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import me.dio.wow_character_interaction.configs.TestsConfig;
import me.dio.wow_character_interaction.data.dto.security.TokenDto;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@Tag("json")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(
        initializers = AbstractIntegrationTest.Initializer.class
)
@TestPropertySource("classpath:application-test.properties")
public class UserControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static TestUserDto userDto;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        userDto = new TestUserDto();
    }

    @Test
    @Order(0)
    public void createUser() throws IOException {
        mockUser();

        var content = given()
                        .basePath("/api/v1/users")
                        .port(TestsConfig.SERVER_PORT)
                        .contentType(TestsConfig.CONTENT_TYPE_JSON)
                            .body(userDto)
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
    public void authorization() throws IOException {
        TestAccountCredentialsDto credentialsDto = new TestAccountCredentialsDto("TestName",
                                                "TestPassword123");

        var content = given()
                            .basePath("/api/v1/auth")
                            .port(TestsConfig.SERVER_PORT)
                            .contentType(TestsConfig.CONTENT_TYPE_JSON)
                                .body(credentialsDto)
                            .when()
                                .post("/signin")
                            .then()
                                .statusCode(200)
                            .extract()
                                .body()
                                    .asString();

        String token = objectMapper.readValue(content, TestTokenDto.class).getAccessToken();

        specification = new RequestSpecBuilder()
                                .addHeader(TestsConfig.HEADER_PARAM_AUTHORIZATION, "Bearer " + token)
                                .setBasePath("/api/v1/users")
                                .setPort(TestsConfig.SERVER_PORT)
                                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                                .build();
    }

    @Test
    @Order(2)
    public void updateUserFullName() throws IOException {

        var content = given()
                        .spec(specification)
                        .pathParam("username", "TestName")
                        .contentType(TestsConfig.CONTENT_TYPE_JSON)
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
        assertNotNull(updatedUser.getPassword());

        assertEquals("TestName", updatedUser.getUsername());
        assertEquals("Test Updated Full Name", updatedUser.getFullName());
    }

    @Test
    @Order(3)
    public void updateUserPassword() throws IOException {

        var content = given()
                        .spec(specification)
                        .pathParam("username", "TestName")
                        .contentType(TestsConfig.CONTENT_TYPE_JSON)
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
        assertNotNull(updatedUser.getPassword());

        assertEquals("TestName", updatedUser.getUsername());
        assertTrue(passwordEncoder.matches("TestUpdatedPassword123", updatedUser.getPassword()));
    }

    public void mockUser() {
        userDto.setUsername("TestName");
        userDto.setFullName("Test Full Name");
        userDto.setPassword("TestPassword123");
    }
}
