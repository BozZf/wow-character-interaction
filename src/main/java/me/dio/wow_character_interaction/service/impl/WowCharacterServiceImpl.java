package me.dio.wow_character_interaction.service.impl;

import me.dio.wow_character_interaction.domain.model.WowCharacter;
import me.dio.wow_character_interaction.adapter.repository.WowCharacterRepository;
import me.dio.wow_character_interaction.service.WowCharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WowCharacterServiceImpl implements WowCharacterService {

    private final WowCharacterRepository wowCharacterRepository;

    public WowCharacterServiceImpl(WowCharacterRepository wowCharacterRepository) {
        this.wowCharacterRepository = wowCharacterRepository;
    }

    @Override
    public List<WowCharacter> findAllWowCharacters() {
        return wowCharacterRepository.findAll();
    }

    @Override
    public WowCharacter findWowCharacterById(Long id) {
        return wowCharacterRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public WowCharacter createWowCharacter(WowCharacter wowCharacterToCreate) {
        if (wowCharacterToCreate.getName() != null &&
                wowCharacterRepository.existsByName(wowCharacterToCreate.getName())) {
            throw new IllegalArgumentException("This character already exist.");
        }
        return wowCharacterRepository.save(wowCharacterToCreate);
    }

    @Override
    public WowCharacter updateWowCharacter(Long id, WowCharacter wowCharacterToUpdate) {
        if (id != null && !wowCharacterRepository.existsById(id)) {
            throw new NoSuchElementException();
        }
        return wowCharacterRepository.save(wowCharacterToUpdate);
    }
}
