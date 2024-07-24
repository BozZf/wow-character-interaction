package me.dio.wow_character_interaction.integrationtests.controller.withjson;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import me.dio.wow_character_interaction.configs.TestsConfig;
import me.dio.wow_character_interaction.integrationtests.dto.WowCharacterDTO;
import me.dio.wow_character_interaction.integrationtests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = "server.port:8888")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DtoControllerTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static WowCharacterDTO dto;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        dto = new WowCharacterDTO();
    }

    @Test
    @Order(1)
    public void testCreate() throws IOException {
        mockDto();

        specification = new RequestSpecBuilder()
                .addHeader(TestsConfig.HEADER_PARAM_ORIGIN, TestsConfig.ORIGIN_8888)
                .setBasePath("/api/character/v1")
                .setPort(TestsConfig.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given().spec(specification)
                        .contentType(TestsConfig.CONTENT_TYPE_JSON)
                            .body(dto)
                        .when()
                            .post()
                        .then()
                            .statusCode(200)
                        .extract()
                            .body()
                                .asString();

        WowCharacterDTO createdCharacter = objectMapper.readValue(content, WowCharacterDTO.class);
        dto = createdCharacter;

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

    @Test
    @Order(2)
    public void testCreateWithWrongOrigin() throws IOException {
        mockDto();

        specification = new RequestSpecBuilder()
                .addHeader(TestsConfig.HEADER_PARAM_ORIGIN, TestsConfig.ORIGIN_3030)
                .setBasePath("/api/character/v1")
                .setPort(TestsConfig.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given().spec(specification)
                        .contentType(TestsConfig.CONTENT_TYPE_JSON)
                            .body(dto)
                        .when()
                            .post()
                        .then()
                            .statusCode(403)
                        .extract()
                            .body()
                                .asString();

        assertNotNull(content);

        assertEquals("Invalid CORS request", content);
    }

    private void mockDto() {
        dto.setName("TestName");
        dto.setGender("TestGender");
        dto.setRace("TestRace");
        dto.setOccupation("TestOccupation");
        dto.setLore("TestLore");
    }
}
