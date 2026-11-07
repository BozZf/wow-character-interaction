package me.dio.wow_character_interaction.application;

import me.dio.wow_character_interaction.adapter.repository.WowCharacterRepository;
import me.dio.wow_character_interaction.domain.model.WowCharacter;
import me.dio.wow_character_interaction.service.GeminiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AskCharacterUseCaseTest {

    private final WowCharacterRepository characters = mock(WowCharacterRepository.class);
    private final GeminiService gemini = mock(GeminiService.class);
    private final AskCharacterUseCase useCase = new AskCharacterUseCase(characters, gemini);

    @Test
    void forwardsTheQuestionAndCharacterContext() {
        WowCharacter character = new WowCharacter();
        character.setName("Jaina Proudmoore");
        character.setLore("A mage from Kul Tiras");
        when(characters.findById(1L)).thenReturn(Optional.of(character));
        when(gemini.askCharacterQuestion(contains("World Of Warcraft"), argThat(context ->
                context.contains("Question: Who are you?")
                        && context.contains("Character Name: Jaina Proudmoore")
                        && context.contains("Lore: A mage from Kul Tiras")))).thenReturn("I am Jaina.");

        assertEquals("I am Jaina.", useCase.askCharacter(1L, "Who are you?"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void rejectsBlankQuestionsBeforeCallingTheRepositoryOrGemini(String question) {
        assertThrows(IllegalArgumentException.class, () -> useCase.askCharacter(1L, question));
        verifyNoInteractions(characters, gemini);
    }

    @Test
    void reportsAnUnknownCharacterWithoutCallingGemini() {
        when(characters.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> useCase.askCharacter(999L, "Who are you?"));
        verifyNoInteractions(gemini);
    }
}
