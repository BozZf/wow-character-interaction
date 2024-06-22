package me.dio.wow_character_interaction.application;

import me.dio.wow_character_interaction.adapter.repository.WowCharacterRepository;
import me.dio.wow_character_interaction.domain.model.WowCharacter;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public record AskCharacterUseCase(WowCharacterRepository wowCharacterRepository) {

    public String askCharacter(Long id, String question) {

        WowCharacter wowCharacter = wowCharacterRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        return wowCharacter.generateContextByQuestion(question);
    }
}
