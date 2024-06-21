package me.dio.wow_character_interaction.service;

import me.dio.wow_character_interaction.domain.model.WowCharacter;

import java.util.List;

public interface WowCharacterService {

    List<WowCharacter> findAllWowCharacters();

    WowCharacter findWowCharacterById(Long id);

    WowCharacter createWowCharacter(WowCharacter wowCharacterToCreate);

    WowCharacter updateWowCharacter(Long id, WowCharacter wowCharacterToUpdate);

    void deleteWowCharacter(Long id);
}
