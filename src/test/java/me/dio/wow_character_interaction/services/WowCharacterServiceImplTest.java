package me.dio.wow_character_interaction.services;

import me.dio.wow_character_interaction.adapter.repository.WowCharacterRepository;
import me.dio.wow_character_interaction.data.dto.WowCharacterDto;
import me.dio.wow_character_interaction.domain.model.WowCharacter;
import me.dio.wow_character_interaction.mapper.mocks.MockWowCharacter;
import me.dio.wow_character_interaction.service.impl.WowCharacterServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WowCharacterServiceImplTest {

    private final MockWowCharacter input = new MockWowCharacter();

    @InjectMocks
    private WowCharacterServiceImpl service;

    @Mock
    private WowCharacterRepository repository;

    @Test
    void listsMappedCharacters() {
        when(repository.findAll()).thenReturn(input.mockEntityList());

        assertEquals(input.mockDTOList(), service.findAllWowCharacters());
    }

    @Test
    void findsCharacterById() {
        when(repository.findById(1L)).thenReturn(Optional.of(input.mockEntity(1)));

        assertEquals(input.mockDTO(1), service.findWowCharacterById(1L));
    }

    @Test
    void rejectsUnknownCharacter() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.findWowCharacterById(1L));
    }

    @Test
    void createsWithAnIdAssignedByTheRepository() {
        when(repository.save(any(WowCharacter.class))).thenAnswer(invocation -> {
            WowCharacter entity = invocation.getArgument(0);
            assertNull(entity.getId());
            entity.setId(2L);
            return entity;
        });

        WowCharacterDto created = service.createWowCharacter(input.mockDTO(1));

        assertEquals(2L, created.getKey());
        assertEquals("Name Test1", created.getName());
    }

    @Test
    void rejectsDuplicateNameBeforeSaving() {
        when(repository.existsByName("Name Test1")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.createWowCharacter(input.mockDTO(1)));
        verify(repository, never()).save(any());
    }

    @Test
    void updatesOnlyFieldsPresentInTheRequest() {
        WowCharacter character = input.mockEntity(1);
        WowCharacterDto changes = new WowCharacterDto();
        changes.setName("Updated name");
        when(repository.findById(1L)).thenReturn(Optional.of(character));
        when(repository.save(character)).thenReturn(character);

        WowCharacterDto updated = service.updateWowCharacter(1L, changes);

        assertEquals(1L, updated.getKey());
        assertEquals("Updated name", updated.getName());
        assertEquals("Lore Test1", updated.getLore());
    }
}
