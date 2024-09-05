package me.dio.wow_character_interaction;

import me.dio.wow_character_interaction.integrationtests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ContextConfiguration(
		initializers = AbstractIntegrationTest.Initializer.class
)
@TestPropertySource("classpath:application-test.properties")
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
