package me.dio.wow_character_interaction.application;

import me.dio.wow_character_interaction.adapter.repository.WowCharacterRepository;
import me.dio.wow_character_interaction.domain.model.WowCharacter;
import me.dio.wow_character_interaction.service.GeminiService;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public record AskCharacterUseCase(WowCharacterRepository wowCharacterRepository, GeminiService geminiService) {

    public String askCharacter(Long id, String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question is required");
        }

        WowCharacter wowCharacter = wowCharacterRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        String objective = """
                Act like you are a World Of Warcraft(WOW) character.
                Answer the questions incorporating the persona of a certain character.
                Here's the question, the infos of the character and his lore:
                """;
        String context = wowCharacter.generateContextByQuestion(question);

        return geminiService.askCharacterQuestion(objective, context);
    }
}
