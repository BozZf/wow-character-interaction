package me.dio.wow_character_interaction.integrationtests.controller.withjson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import me.dio.wow_character_interaction.configs.TestsConfig;
import me.dio.wow_character_interaction.integrationtests.dto.TestAccountCredentialsDto;
import me.dio.wow_character_interaction.integrationtests.dto.TestTokenDto;
import me.dio.wow_character_interaction.integrationtests.dto.TestWowCharacterDTO;
import me.dio.wow_character_interaction.integrationtests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(
        initializers = AbstractIntegrationTest.Initializer.class
)
@TestPropertySource("classpath:application-test.properties")
public class CharacterControllerTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static TestWowCharacterDTO characterDto;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        characterDto = new TestWowCharacterDTO();
    }

    @Test
    @Order(0)
    public void authorization() throws JsonMappingException, JsonProcessingException {
        TestAccountCredentialsDto user = new TestAccountCredentialsDto("Admin", "Admin123");

        var accessToken = given()
                .basePath("/api/v1/auth/signin")
                    .port(TestsConfig.SERVER_PORT)
                    .contentType(TestsConfig.CONTENT_TYPE_JSON)
                .body(user)
                    .when()
                .post()
                    .then()
                        .statusCode(200)
                            .extract()
                                .body()
                                    .as(TestTokenDto.class)
                                .getAccessToken();

        specification = new RequestSpecBuilder()
                .addHeader(TestsConfig.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken )
                .setBasePath("/api/v1/character/create")
                .setPort(TestsConfig.SERVER_PORT)
                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    public void testCreate() throws IOException {
        mockCharacterDto();

        var content = given().spec(specification)
                        .contentType(TestsConfig.CONTENT_TYPE_JSON)
                        .header(TestsConfig.HEADER_PARAM_ORIGIN, TestsConfig.ORIGIN_8888)
                            .body(characterDto)
                        .when()
                            .post()
                        .then()
                            .statusCode(200)
                        .extract()
                            .body()
                                .asString();

        TestWowCharacterDTO createdCharacter = objectMapper.readValue(content, TestWowCharacterDTO.class);

        assertNotNull(createdCharacter);
        assertNotNull(createdCharacter.getId());
        assertNotNull(createdCharacter.getName());
        assertNotNull(createdCharacter.getGender());
        assertNotNull(createdCharacter.getRace());
        assertNotNull(createdCharacter.getOccupation());
        assertNotNull(createdCharacter.getLore());

        assertTrue(createdCharacter.getId() > 0);

        assertEquals("TestName", createdCharacter.getName());
        assertEquals("TestGender", createdCharacter.getGender());
        assertEquals("TestRace", createdCharacter.getRace());
        assertEquals("TestOccupation", createdCharacter.getOccupation());
        assertEquals("TestLore", createdCharacter.getLore());
    }

    private void mockCharacterDto() {
        characterDto.setName("TestName");
        characterDto.setGender("TestGender");
        characterDto.setRace("TestRace");
        characterDto.setOccupation("TestOccupation");
        characterDto.setLore("TestLore");
    }
}
