package me.dio.wow_character_interaction.service;

import me.dio.wow_character_interaction.data.dto.WowCharacterDto;

import java.util.List;

public interface WowCharacterService {

    List<WowCharacterDto> findAllWowCharacters();

    WowCharacterDto findWowCharacterById(Long id);

    WowCharacterDto createWowCharacter(WowCharacterDto wowCharacterToCreate);

    WowCharacterDto updateWowCharacter(Long id, WowCharacterDto wowCharacterToUpdate);

    void deleteWowCharacter(Long id);
}
