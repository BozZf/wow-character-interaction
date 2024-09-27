package me.dio.wow_character_interaction.integrationtests.controller.withxml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import me.dio.wow_character_interaction.configs.TestsConfig;
import me.dio.wow_character_interaction.integrationtests.dto.*;
import me.dio.wow_character_interaction.integrationtests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@Tag("xml")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(
        initializers = AbstractIntegrationTest.Initializer.class
)
@TestPropertySource("classpath:application-test.properties")
public class CharacterControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static XmlMapper objectMapper;

    private static TestWowCharacterDTO characterDto;

    @BeforeAll
    public static void setup() {
        objectMapper = new XmlMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        characterDto = new TestWowCharacterDTO();
    }

    @Test
    @Order(0)
    public void authorization() throws JsonMappingException, JsonProcessingException {
        TestAccountCredentialsDto user = new TestAccountCredentialsDto("TestAdmin", "TestAdmin123");

        var content = given()
                            .basePath("/api/v1/auth")
                            .port(TestsConfig.SERVER_PORT)
                            .contentType(TestsConfig.CONTENT_TYPE_XML)
                            .accept(TestsConfig.CONTENT_TYPE_XML)
                            .body(objectMapper.writeValueAsString(user))
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
                                .setBasePath("/api/v1/character")
                                .setPort(TestsConfig.SERVER_PORT)
                                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                                .build();
    }

    @Test
    @Order(1)
    public void testCreate() throws IOException {
        mockCharacterDto();

        var content = given()
                        .spec(specification)
                        .contentType(TestsConfig.CONTENT_TYPE_XML)
                        .accept(TestsConfig.CONTENT_TYPE_XML)
                            .body(objectMapper.writeValueAsString(characterDto))
                        .when()
                            .post("/create")
                        .then()
                            .statusCode(200)
                        .extract()
                            .body()
                                .asString();

        TestWowCharacterDTO createdCharacter = objectMapper.readValue(content, TestWowCharacterDTO.class);

        assertNotNull(createdCharacter);
        assertNotNull(createdCharacter);
        assertNotNull(createdCharacter.getId());
        assertNotNull(createdCharacter.getName());
        assertNotNull(createdCharacter.getGender());
        assertNotNull(createdCharacter.getRace());
        assertNotNull(createdCharacter.getCharacterClass());
        assertNotNull(createdCharacter.getOccupation());
        assertNotNull(createdCharacter.getLore());

        assertTrue(createdCharacter.getId() > 0);

        assertEquals("TestNameXml", createdCharacter.getName());
        assertEquals("TestGenderXml", createdCharacter.getGender());
        assertEquals("TestRaceXml", createdCharacter.getRace());
        assertEquals("TestClassXml", createdCharacter.getCharacterClass());
        assertEquals("TestOccupationXml", createdCharacter.getOccupation());
        assertEquals("TestLoreXml", createdCharacter.getLore());
    }

    @Test
    @Order(2)
    public void testFindAll() throws IOException {

        var content = given()
                            .spec(specification)
                            .contentType(TestsConfig.CONTENT_TYPE_XML)
                            .accept(TestsConfig.CONTENT_TYPE_XML)
                            .when()
                                .get("/find-all")
                            .then()
                                .statusCode(200)
                            .extract()
                                .body()
                                    .asString();

        List<TestWowCharacterDTO> characters = Arrays.asList(objectMapper
                .readValue(content, TestWowCharacterDTO[].class));

        assertEquals(10, characters.size());

        TestWowCharacterDTO characterTest = characters.get(9);

        assertNotNull(characterTest);
        assertNotNull(characterTest.getId());
        assertNotNull(characterTest.getName());
        assertNotNull(characterTest.getGender());
        assertNotNull(characterTest.getRace());
        assertNotNull(characterTest.getCharacterClass());
        assertNotNull(characterTest.getOccupation());
        assertNotNull(characterTest.getLore());

        assertEquals(10L, characterTest.getId());
        assertEquals("TestNameXml", characterTest.getName());
        assertEquals("TestGenderXml", characterTest.getGender());
        assertEquals("TestRaceXml", characterTest.getRace());
        assertEquals("TestClassXml", characterTest.getCharacterClass());
        assertEquals("TestOccupationXml", characterTest.getOccupation());
        assertEquals("TestLoreXml", characterTest.getLore());
    }

    @Test
    @Order(3)
    public void testFindById() throws IOException {

        var content = given().spec(specification)
                .contentType(TestsConfig.CONTENT_TYPE_XML)
                .accept(TestsConfig.CONTENT_TYPE_XML)
                .pathParam("id", 10L)
                .when()
                    .get("/find/{id}")
                .then()
                    .statusCode(200)
                .extract()
                    .body()
                        .asString();

        TestWowCharacterDTO characterFound = objectMapper.readValue(content, TestWowCharacterDTO.class);

        assertNotNull(characterFound);
        assertNotNull(characterFound.getId());
        assertNotNull(characterFound.getName());
        assertNotNull(characterFound.getGender());
        assertNotNull(characterFound.getRace());
        assertNotNull(characterFound.getCharacterClass());
        assertNotNull(characterFound.getOccupation());
        assertNotNull(characterFound.getLore());

        assertEquals(10L, characterFound.getId());
        assertEquals("TestNameXml", characterFound.getName());
        assertEquals("TestGenderXml", characterFound.getGender());
        assertEquals("TestRaceXml", characterFound.getRace());
        assertEquals("TestClassXml", characterFound.getCharacterClass());
        assertEquals("TestOccupationXml", characterFound.getOccupation());
        assertEquals("TestLoreXml", characterFound.getLore());
    }

    @Test
    @Order(4)
    public void testAsk() throws  IOException {
        TestAskCharacterRequestDto request = new TestAskCharacterRequestDto("This is just a test");

        var content = given()
                .spec(specification)
                .contentType(TestsConfig.CONTENT_TYPE_XML)
                .accept(TestsConfig.CONTENT_TYPE_XML)
                .pathParam("id", 1L)
                    .body(objectMapper.writeValueAsString(request))
                .when()
                    .post("/ask/{id}")
                .then()
                    .statusCode(200)
                .extract()
                    .body()
                        .asString();

        String testAnswer = objectMapper.readValue(content, TestAskCharacterResponseDto.class).getAnswer();

        assertNotNull(testAnswer);
    }

    @Test
    @Order(5)
    public void testUpdate() throws IOException {
        updateMockedCharacterDto();

        var content = given()
                .spec(specification)
                .contentType(TestsConfig.CONTENT_TYPE_XML)
                .accept(TestsConfig.CONTENT_TYPE_XML)
                .pathParam("id", 10L)
                .body(objectMapper.writeValueAsString(characterDto))
                .when()
                    .put("/update/{id}")
                .then()
                    .statusCode(200)
                .extract()
                    .body()
                        .asString();

        TestWowCharacterDTO updatedCharacter = objectMapper.readValue(content, TestWowCharacterDTO.class);

        assertNotNull(updatedCharacter);
        assertNotNull(updatedCharacter.getId());
        assertNotNull(updatedCharacter.getName());
        assertNotNull(updatedCharacter.getGender());
        assertNotNull(updatedCharacter.getRace());
        assertNotNull(updatedCharacter.getCharacterClass());
        assertNotNull(updatedCharacter.getOccupation());
        assertNotNull(updatedCharacter.getLore());

        assertEquals(10L, updatedCharacter.getId());
        assertEquals("TestUpdatedNameXml", updatedCharacter.getName());
        assertEquals("TestUpdatedGenderXml", updatedCharacter.getGender());
        assertEquals("TestUpdatedRaceXml", updatedCharacter.getRace());
        assertEquals("TestUpdatedClassXml", updatedCharacter.getCharacterClass());
        assertEquals("TestUpdatedOccupationXml", updatedCharacter.getOccupation());
        assertEquals("TestUpdatedLoreXml", updatedCharacter.getLore());
    }

    @Test
    @Order(6)
    public void testDelete() throws IOException {
        updateMockedCharacterDto();

        var content = given()
                .spec(specification)
                .contentType(TestsConfig.CONTENT_TYPE_XML)
                .pathParam("id", 10L)
                .when()
                    .delete("/delete/{id}")
                .then()
                    .statusCode(204);
    }

    private void mockCharacterDto() {
        characterDto.setName("TestNameXml");
        characterDto.setGender("TestGenderXml");
        characterDto.setRace("TestRaceXml");
        characterDto.setCharacterClass("TestClassXml");
        characterDto.setOccupation("TestOccupationXml");
        characterDto.setLore("TestLoreXml");
    }

    private void updateMockedCharacterDto() {
        characterDto.setName("TestUpdatedNameXml");
        characterDto.setGender("TestUpdatedGenderXml");
        characterDto.setRace("TestUpdatedRaceXml");
        characterDto.setCharacterClass("TestUpdatedClassXml");
        characterDto.setOccupation("TestUpdatedOccupationXml");
        characterDto.setLore("TestUpdatedLoreXml");
    }
}
