package me.dio.wow_character_interaction.integrationtests.swagger;

import me.dio.wow_character_interaction.configs.TestsConfig;
import me.dio.wow_character_interaction.integrationtests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SwaggerIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void shouldDisplaySwaggerUiPage() {
        var content = given()
                        .basePath("/swagger-ui/index.html")
                        .port(TestsConfig.SERVER_PORT)
                        .when()
                            .get()
                        .then()
                            .statusCode(200)
                        .extract()
                            .body()
                                .asString();

        assertTrue(content.contains("Swagger UI"));
    }

    @Test
    void documentsAccessAndRefreshBearerSchemes() {
        given().port(TestsConfig.SERVER_PORT)
                .when().get("/v3/api-docs")
                .then().statusCode(200)
                .body("components.securitySchemes.bearerAuth.scheme", equalTo("bearer"))
                .body("components.securitySchemes.refreshBearerAuth.scheme", equalTo("bearer"));
    }
}
