package me.dio.wow_character_interaction.mapper.mocks;

import me.dio.wow_character_interaction.data.dto.WowCharacterDTO;
import me.dio.wow_character_interaction.domain.model.WowCharacter;

import java.util.ArrayList;
import java.util.List;

public class MockWowCharacter {

    public WowCharacter mockEntity() {
        return mockEntity(0);
    }

    public WowCharacterDTO mockDTO() {
        return mockDTO(0);
    }

    public List<WowCharacter> mockEntityList() {
        List<WowCharacter> characters = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            characters.add(mockEntity(i));
        }
        return characters;
    }

    public List<WowCharacterDTO> mockDTOList() {
        List<WowCharacterDTO> characters = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            characters.add(mockDTO(i));
        }
        return characters;
    }

    public WowCharacter mockEntity(Integer number) {
        WowCharacter character = new WowCharacter();
        character.setId(number.longValue());
        character.setName("Name Test" + number);
        character.setGender((number % 2) == 0 ? "Male Test" : "Female Test");
        character.setRace("Race Test" + number);
        character.setCharacterClass("Class Test" + number);
        character.setOccupation("Occupation Test" + number);
        character.setLore("Lore Test" + number);
        return character;
    }

    public WowCharacterDTO mockDTO(Integer number) {
        WowCharacterDTO characterDTO = new WowCharacterDTO();
        characterDTO.setKey(number.longValue());
        characterDTO.setName("Name Test" + number);
        characterDTO.setGender((number % 2) == 0 ? "Male Test" : "Female Test");
        characterDTO.setRace("Race Test" + number);
        characterDTO.setCharacterClass("Class Test" + number);
        characterDTO.setOccupation("Occupation Test" + number);
        characterDTO.setLore("Lore Test" + number);
        return characterDTO;
    }
}
