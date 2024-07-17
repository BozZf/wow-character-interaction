package me.dio.wow_character_interaction.services;

import me.dio.wow_character_interaction.adapter.controller.exception.RequiredObjectIsNull;
import me.dio.wow_character_interaction.adapter.repository.WowCharacterRepository;
import me.dio.wow_character_interaction.data.dto.WowCharacterDTO;
import me.dio.wow_character_interaction.domain.model.WowCharacter;
import me.dio.wow_character_interaction.mapper.mocks.MockWowCharacter;
import me.dio.wow_character_interaction.service.impl.WowCharacterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class WowCharacterServiceImplTest {

    private MockWowCharacter input;

    @InjectMocks
    private WowCharacterServiceImpl wowCharacterService;

    @Mock
    private WowCharacterRepository wowCharacterRepository;

    @BeforeEach
    void setUpMocks() throws Exception {
        input = new MockWowCharacter();
    }

    @Test
    void findAllWowCharacters() {
        List<WowCharacter> list = input.mockEntityList();

        when(wowCharacterRepository.findAll()).thenReturn(list);

        var results = wowCharacterService.findAllWowCharacters();

        assertNotNull(results);
        assertEquals(7, results.size());

        var character1 = results.get(1);
        assertNotNull(character1);
        assertNotNull(character1.getKey());
        assertNotNull(character1.getLinks());
        assertTrue(character1.toString().contains("links: [</api/character/v1/1>;rel=\"self\"," +
                " </api/character/v1/1>;rel=\"Update Name Test1 Information\"," +
                " </api/character/v1>;rel=\"Ask a Question to Name Test1\"]"));
        assertEquals("Name Test1", character1.getName());
        assertEquals("Female Test", character1.getGender());
        assertEquals("Race Test1", character1.getRace());
        assertEquals("Class Test1", character1.getCharacterClass());
        assertEquals("Occupation Test1", character1.getOccupation());
        assertEquals("Lore Test1", character1.getLore());

        var character3 = results.get(3);
        assertNotNull(character3);
        assertNotNull(character3.getKey());
        assertNotNull(character3.getLinks());
        assertTrue(character3.toString().contains("links: [</api/character/v1/3>;rel=\"self\"," +
                " </api/character/v1/3>;rel=\"Update Name Test3 Information\"," +
                " </api/character/v1>;rel=\"Ask a Question to Name Test3\"]"));
        assertEquals("Name Test3", character3.getName());
        assertEquals("Female Test", character3.getGender());
        assertEquals("Race Test3", character3.getRace());
        assertEquals("Class Test3", character3.getCharacterClass());
        assertEquals("Occupation Test3", character3.getOccupation());
        assertEquals("Lore Test3", character3.getLore());

        var character6 = results.get(6);
        assertNotNull(character6);
        assertNotNull(character6.getKey());
        assertNotNull(character6.getLinks());
        assertTrue(character6.toString().contains("links: [</api/character/v1/6>;rel=\"self\"," +
                " </api/character/v1/6>;rel=\"Update Name Test6 Information\"," +
                " </api/character/v1>;rel=\"Ask a Question to Name Test6\"]"));
        assertEquals("Name Test6", character6.getName());
        assertEquals("Male Test", character6.getGender());
        assertEquals("Race Test6", character6.getRace());
        assertEquals("Class Test6", character6.getCharacterClass());
        assertEquals("Occupation Test6", character6.getOccupation());
        assertEquals("Lore Test6", character6.getLore());
    }

    @Test
    void findWowCharacterById() {
        WowCharacter character = input.mockEntity(1);
        character.setId(1L);

        when(wowCharacterRepository.findById(1L)).thenReturn(Optional.of(character));

        var result = wowCharacterService.findWowCharacterById(1L);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/character/v1/1>;rel=\"self\"," +
                " </api/character/v1/1>;rel=\"Update Name Test1 Information\"," +
                " </api/character/v1>;rel=\"Ask a Question to Name Test1\"]"));
        assertEquals("Name Test1", result.getName());
        assertEquals("Female Test", result.getGender());
        assertEquals("Race Test1", result.getRace());
        assertEquals("Class Test1", result.getCharacterClass());
        assertEquals("Occupation Test1", result.getOccupation());
        assertEquals("Lore Test1", result.getLore());
    }

    @Test
    void createWowCharacter() {
        WowCharacter character = input.mockEntity(1);
        WowCharacterDTO dto = input.mockDTO(1);

        when(wowCharacterRepository.save(character)).thenReturn(character);

        var result = wowCharacterService.createWowCharacter(dto);

        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());

        assertTrue(result.toString().contains("links: [</api/character/v1/1>;rel=\"self\"," +
                " </api/character/v1/1>;rel=\"Update Name Test1 Information\"," +
                " </api/character/v1>;rel=\"Ask a Question to Name Test1\"]"));

        assertEquals("Name Test1", result.getName());
        assertEquals("Female Test", result.getGender());
        assertEquals("Race Test1", result.getRace());
        assertEquals("Class Test1", result.getCharacterClass());
        assertEquals("Occupation Test1", result.getOccupation());
        assertEquals("Lore Test1", result.getLore());
    }

    @Test
    void updateWowCharacter() {
        WowCharacter character = input.mockEntity(1);
        character.setId(1L);
        WowCharacterDTO dto = input.mockDTO(1);
        dto.setKey(1L);

        when(wowCharacterRepository.findById(1L)).thenReturn(Optional.of(character));
        when(wowCharacterRepository.save(character)).thenReturn(character);

        var result = wowCharacterService.updateWowCharacter(1L, dto);

        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());

        assertTrue(result.toString().contains("links: [</api/character/v1/1>;rel=\"self\"," +
                " </api/character/v1/1>;rel=\"Update Name Test1 Information\"," +
                " </api/character/v1>;rel=\"Ask a Question to Name Test1\"]"));

        assertEquals("Name Test1", result.getName());
        assertEquals("Female Test", result.getGender());
        assertEquals("Race Test1", result.getRace());
        assertEquals("Class Test1", result.getCharacterClass());
        assertEquals("Occupation Test1", result.getOccupation());
        assertEquals("Lore Test1", result.getLore());
    }
}
