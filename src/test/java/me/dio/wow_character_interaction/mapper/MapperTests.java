package me.dio.wow_character_interaction.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.dio.wow_character_interaction.domain.model.WowCharacter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import me.dio.wow_character_interaction.data.dto.WowCharacterDTO;
import me.dio.wow_character_interaction.mapper.mocks.MockWowCharacter;

import java.util.List;

public class MapperTests {

    MockWowCharacter inputObject;

    @BeforeEach
    public void setUp() {
        inputObject = new MockWowCharacter();
    }

    @Test
    public void parseEntityToDTOTest() {
        WowCharacterDTO output = DTOMapper.parseObject(inputObject.mockEntity(), WowCharacterDTO.class);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("Name Test0", output.getName());
        assertEquals("Male Test", output.getGender());
        assertEquals("Race Test0", output.getRace());
        assertEquals("Class Test0", output.getCharacterClass());
        assertEquals("Occupation Test0", output.getOccupation());
        assertEquals("Lore Test0", output.getLore());
    }

    @Test
    public void parseEntityListToDTOListTest() {
        List<WowCharacterDTO> outputList = DTOMapper.parseListObjects(inputObject.mockEntityList(),
                WowCharacterDTO.class);

        WowCharacterDTO outputZero = outputList.get(0);
        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("Name Test0", outputZero.getName());
        assertEquals("Male Test", outputZero.getGender());
        assertEquals("Race Test0", outputZero.getRace());
        assertEquals("Class Test0", outputZero.getCharacterClass());
        assertEquals("Occupation Test0", outputZero.getOccupation());
        assertEquals("Lore Test0", outputZero.getLore());

        WowCharacterDTO outputThree = outputList.get(3);
        assertEquals(Long.valueOf(3L), outputThree.getId());
        assertEquals("Name Test3", outputThree.getName());
        assertEquals("Female Test", outputThree.getGender());
        assertEquals("Race Test3", outputThree.getRace());
        assertEquals("Class Test3", outputThree.getCharacterClass());
        assertEquals("Occupation Test3", outputThree.getOccupation());
        assertEquals("Lore Test3", outputThree.getLore());

        WowCharacterDTO outputSix = outputList.get(6);
        assertEquals(Long.valueOf(6L), outputSix.getId());
        assertEquals("Name Test6", outputSix.getName());
        assertEquals("Male Test", outputSix.getGender());
        assertEquals("Race Test6", outputSix.getRace());
        assertEquals("Class Test6", outputSix.getCharacterClass());
        assertEquals("Occupation Test6", outputSix.getOccupation());
        assertEquals("Lore Test6", outputSix.getLore());
    }

    @Test
    public void parseDTOToEntityTest() {
        WowCharacter output = DTOMapper.parseObject(inputObject.mockDTO(), WowCharacter.class);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("Name Test0", output.getName());
        assertEquals("Male Test", output.getGender());
        assertEquals("Race Test0", output.getRace());
        assertEquals("Class Test0", output.getCharacterClass());
        assertEquals("Occupation Test0", output.getOccupation());
        assertEquals("Lore Test0", output.getLore());
    }

    @Test
    public void parseDTOListToEntityListTest() {
        List<WowCharacter> outputList = DTOMapper.parseListObjects(inputObject.mockDTOList(),
                WowCharacter.class);

        WowCharacter outputZero = outputList.get(0);
        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("Name Test0", outputZero.getName());
        assertEquals("Male Test", outputZero.getGender());
        assertEquals("Race Test0", outputZero.getRace());
        assertEquals("Class Test0", outputZero.getCharacterClass());
        assertEquals("Occupation Test0", outputZero.getOccupation());
        assertEquals("Lore Test0", outputZero.getLore());

        WowCharacter outputThree = outputList.get(3);
        assertEquals(Long.valueOf(3L), outputThree.getId());
        assertEquals("Name Test3", outputThree.getName());
        assertEquals("Female Test", outputThree.getGender());
        assertEquals("Race Test3", outputThree.getRace());
        assertEquals("Class Test3", outputThree.getCharacterClass());
        assertEquals("Occupation Test3", outputThree.getOccupation());
        assertEquals("Lore Test3", outputThree.getLore());

        WowCharacter outputSix = outputList.get(6);
        assertEquals(Long.valueOf(6L), outputSix.getId());
        assertEquals("Name Test6", outputSix.getName());
        assertEquals("Male Test", outputSix.getGender());
        assertEquals("Race Test6", outputSix.getRace());
        assertEquals("Class Test6", outputSix.getCharacterClass());
        assertEquals("Occupation Test6", outputSix.getOccupation());
        assertEquals("Lore Test6", outputSix.getLore());
    }
}
