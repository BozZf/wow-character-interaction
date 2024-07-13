package me.dio.wow_character_interaction.service;

import me.dio.wow_character_interaction.data.dto.WowCharacterDTO;
import me.dio.wow_character_interaction.domain.model.WowCharacter;

import java.util.List;

public interface WowCharacterService {

    List<WowCharacterDTO> findAllWowCharacters();

    WowCharacterDTO findWowCharacterById(Long id);

    WowCharacterDTO createWowCharacter(WowCharacterDTO wowCharacterToCreate);

    WowCharacterDTO updateWowCharacter(Long id, WowCharacterDTO wowCharacterToUpdate);

    void deleteWowCharacter(Long id);
}
