package me.dio.wow_character_interaction.integrationtests.controller.withjson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@Tag("json")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(
        initializers = AbstractIntegrationTest.Initializer.class
)
@TestPropertySource("classpath:application-test.properties")
public class CharacterControllerJsonTest extends AbstractIntegrationTest {

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
    public void authorization() throws IOException {
        TestAccountCredentialsDto user = new TestAccountCredentialsDto("TestAdmin", "TestAdmin123");

        var content = given()
                            .basePath("/api/v1/auth")
                            .port(TestsConfig.SERVER_PORT)
                            .contentType(TestsConfig.CONTENT_TYPE_JSON)
                            .accept(TestsConfig.CONTENT_TYPE_JSON)
                                .body(user)
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
                        .contentType(TestsConfig.CONTENT_TYPE_JSON)
                        .accept(TestsConfig.CONTENT_TYPE_JSON)
                            .body(characterDto)
                        .when()
                            .post("/create")
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
        assertNotNull(createdCharacter.getCharacterClass());
        assertNotNull(createdCharacter.getOccupation());
        assertNotNull(createdCharacter.getLore());

        assertTrue(createdCharacter.getId() > 0);

        assertEquals("TestNameJson", createdCharacter.getName());
        assertEquals("TestGenderJson", createdCharacter.getGender());
        assertEquals("TestRaceJson", createdCharacter.getRace());
        assertEquals("TestClassJson", createdCharacter.getCharacterClass());
        assertEquals("TestOccupationJson", createdCharacter.getOccupation());
        assertEquals("TestLoreJson", createdCharacter.getLore());
    }

    @Test
    @Order(2)
    public void testFindAll() throws IOException {

        var content = given()
                        .spec(specification)
                        .contentType(TestsConfig.CONTENT_TYPE_JSON)
                        .accept(TestsConfig.CONTENT_TYPE_JSON)
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
        assertEquals("TestNameJson", characterTest.getName());
        assertEquals("TestGenderJson", characterTest.getGender());
        assertEquals("TestRaceJson", characterTest.getRace());
        assertEquals("TestClassJson", characterTest.getCharacterClass());
        assertEquals("TestOccupationJson", characterTest.getOccupation());
        assertEquals("TestLoreJson", characterTest.getLore());
    }

    @Test
    @Order(3)
    public void testFindById() throws IOException {

        var content = given()
                        .spec(specification)
                        .contentType(TestsConfig.CONTENT_TYPE_JSON)
                        .accept(TestsConfig.CONTENT_TYPE_JSON)
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
        assertEquals("TestNameJson", characterFound.getName());
        assertEquals("TestGenderJson", characterFound.getGender());
        assertEquals("TestRaceJson", characterFound.getRace());
        assertEquals("TestClassJson", characterFound.getCharacterClass());
        assertEquals("TestOccupationJson", characterFound.getOccupation());
        assertEquals("TestLoreJson", characterFound.getLore());
    }

    @Test
    @Order(4)
    public void testAsk() throws  IOException {
        TestAskCharacterRequestDto request = new TestAskCharacterRequestDto("This is just a test");

        var content = given()
                        .spec(specification)
                        .contentType(TestsConfig.CONTENT_TYPE_JSON)
                        .accept(TestsConfig.CONTENT_TYPE_JSON)
                        .pathParam("id", 1L)
                            .body(request)
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
                        .contentType(TestsConfig.CONTENT_TYPE_JSON)
                        .accept(TestsConfig.CONTENT_TYPE_JSON)
                        .pathParam("id", 10L)
                            .body(characterDto)
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
        assertEquals("TestUpdatedNameJson", updatedCharacter.getName());
        assertEquals("TestUpdatedGenderJson", updatedCharacter.getGender());
        assertEquals("TestUpdatedRaceJson", updatedCharacter.getRace());
        assertEquals("TestUpdatedClassJson", updatedCharacter.getCharacterClass());
        assertEquals("TestUpdatedOccupationJson", updatedCharacter.getOccupation());
        assertEquals("TestUpdatedLoreJson", updatedCharacter.getLore());
    }

    @Test
    @Order(6)
    public void testDelete() throws IOException {
        updateMockedCharacterDto();

        var content = given()
                        .spec(specification)
                        .contentType(TestsConfig.CONTENT_TYPE_JSON)
                        .pathParam("id", 10L)
                        .when()
                            .delete("/delete/{id}")
                        .then()
                            .statusCode(204);
    }

    private void mockCharacterDto() {
        characterDto.setName("TestNameJson");
        characterDto.setGender("TestGenderJson");
        characterDto.setRace("TestRaceJson");
        characterDto.setCharacterClass("TestClassJson");
        characterDto.setOccupation("TestOccupationJson");
        characterDto.setLore("TestLoreJson");
    }

    private void updateMockedCharacterDto() {
        characterDto.setName("TestUpdatedNameJson");
        characterDto.setGender("TestUpdatedGenderJson");
        characterDto.setRace("TestUpdatedRaceJson");
        characterDto.setCharacterClass("TestUpdatedClassJson");
        characterDto.setOccupation("TestUpdatedOccupationJson");
        characterDto.setLore("TestUpdatedLoreJson");
    }
}
