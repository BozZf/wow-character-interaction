package me.dio.wow_character_interaction.integrationtests.testcontainers;

import me.dio.wow_character_interaction.service.GeminiService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
@TestPropertySource("classpath:application-test.properties")
public abstract class AbstractIntegrationTest {

    protected static final String GEMINI_ANSWER = "For Azeroth!";

    @MockBean
    protected GeminiService geminiService;

    @BeforeEach
    void stubGemini() {
        when(geminiService.askCharacterQuestion(anyString(), anyString())).thenReturn(GEMINI_ANSWER);
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            var environment = context.getEnvironment();
            String url = environment.getProperty("TEST_POSTGRES_URL");
            Map<String, Object> connection;
            if (url == null || url.isBlank()) {
                var postgres = TestDatabase.CONTAINER;
                postgres.start();
                connection = Map.of(
                        "spring.datasource.url", postgres.getJdbcUrl(),
                        "spring.datasource.username", postgres.getUsername(),
                        "spring.datasource.password", postgres.getPassword());
            } else {
                connection = Map.of(
                        "spring.datasource.url", url,
                        "spring.datasource.username", environment.getRequiredProperty("TEST_POSTGRES_USER"),
                        "spring.datasource.password", environment.getProperty("TEST_POSTGRES_PASSWORD", ""));
            }
            environment.getPropertySources().addFirst(new MapPropertySource("integrationTestDatabase", connection));
        }

        private static class TestDatabase {
            private static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>("postgres:16.3");
        }
    }
}
