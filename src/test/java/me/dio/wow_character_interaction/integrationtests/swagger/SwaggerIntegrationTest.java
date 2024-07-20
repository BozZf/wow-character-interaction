package me.dio.wow_character_interaction.integrationtests.swagger;

import me.dio.wow_character_interaction.configs.DockerTestConfig;
import me.dio.wow_character_interaction.integrationtests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = "server.port=8888")
public class SwaggerIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void shouldDisplaySwaggerUiPage() {
        var content = given()
                        .basePath("/swagger-ui/index.html")
                        .port(DockerTestConfig.SERVER_PORT)
                        .when()
                            .get()
                        .then()
                            .statusCode(200)
                        .extract()
                            .body()
                                .asString();

        assertTrue(content.contains("Swagger UI"));
    }
}
