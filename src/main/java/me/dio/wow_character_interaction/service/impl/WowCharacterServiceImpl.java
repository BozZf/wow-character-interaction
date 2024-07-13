package me.dio.wow_character_interaction.service.impl;

import me.dio.wow_character_interaction.data.dto.WowCharacterDTO;
import me.dio.wow_character_interaction.adapter.repository.WowCharacterRepository;
import me.dio.wow_character_interaction.domain.model.WowCharacter;
import me.dio.wow_character_interaction.mapper.DTOMapper;
import me.dio.wow_character_interaction.service.WowCharacterService;
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
    public List<WowCharacterDTO> findAllWowCharacters() {
        return DTOMapper.parseListObjects(wowCharacterRepository.findAll(), WowCharacterDTO.class);
    }

    @Override
    public WowCharacterDTO findWowCharacterById(Long id) {
        var entity = wowCharacterRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return DTOMapper.parseObject(entity, WowCharacterDTO.class);
    }

    @Override
    public WowCharacterDTO createWowCharacter(WowCharacterDTO wowCharacterToCreate) {
        if (wowCharacterToCreate.getName() != null &&
                wowCharacterRepository.existsByName(wowCharacterToCreate.getName())) {
            throw new IllegalArgumentException();
        }

        var entity = DTOMapper.parseObject(wowCharacterToCreate, WowCharacter.class);
        var dto = DTOMapper.parseObject(wowCharacterRepository.save(entity), WowCharacterDTO.class);

        return dto;
    }

    @Override
    public WowCharacterDTO updateWowCharacter(Long id, WowCharacterDTO wowCharacterToUpdate) {
        if (id != null && !wowCharacterRepository.existsById(id)) {
            throw new NoSuchElementException();
        }
        var entity = DTOMapper.parseObject(wowCharacterToUpdate, WowCharacter.class);
        var dto = DTOMapper.parseObject(wowCharacterRepository.save(entity), WowCharacterDTO.class);

        return dto;
    }

    @Override
    public void deleteWowCharacter(Long id) {
        assert id != null;
        if (!wowCharacterRepository.existsById(id)) {
            throw new NoSuchElementException();
        }
        wowCharacterRepository.deleteById(id);
    }
}
