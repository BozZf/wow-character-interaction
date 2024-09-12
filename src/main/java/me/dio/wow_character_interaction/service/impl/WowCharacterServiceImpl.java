package me.dio.wow_character_interaction.service.impl;

import me.dio.wow_character_interaction.adapter.controller.WowCharacterController;
import me.dio.wow_character_interaction.adapter.controller.exception.RequiredObjectIsNull;
import me.dio.wow_character_interaction.data.dto.WowCharacterDto;
import me.dio.wow_character_interaction.adapter.repository.WowCharacterRepository;
import me.dio.wow_character_interaction.domain.model.WowCharacter;
import me.dio.wow_character_interaction.mapper.DTOMapper;
import me.dio.wow_character_interaction.service.WowCharacterService;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WowCharacterServiceImpl implements WowCharacterService {

    private final WowCharacterRepository wowCharacterRepository;

    public WowCharacterServiceImpl(WowCharacterRepository wowCharacterRepository) {
        this.wowCharacterRepository = wowCharacterRepository;
    }

    @Override
    public List<WowCharacterDto> findAllWowCharacters() {
        var characters = DTOMapper.parseListObjects(wowCharacterRepository.findAll(), WowCharacterDto.class);

        characters.forEach(c -> addLinks(c));

        return characters;
    }

    @Override
    public WowCharacterDto findWowCharacterById(Long id) {
        var entity = wowCharacterRepository.findById(id).orElseThrow(NoSuchElementException::new);
        WowCharacterDto dto = DTOMapper.parseObject(entity, WowCharacterDto.class);

        addLinks(dto);

        return dto;
    }

    @Override
    public WowCharacterDto createWowCharacter(WowCharacterDto wowCharacterToCreateDTO) {
        if (wowCharacterToCreateDTO == null) {
            throw new RequiredObjectIsNull();
        }
        if (wowCharacterToCreateDTO.getName() != null &&
                wowCharacterRepository.existsByName(wowCharacterToCreateDTO.getName())) {
            throw new IllegalArgumentException();
        }

        var entity = DTOMapper.parseObject(wowCharacterToCreateDTO, WowCharacter.class);
        var dto = DTOMapper.parseObject(wowCharacterRepository.save(entity), WowCharacterDto.class);

        addLinks(dto);

        return dto;
    }

    @Override
    public WowCharacterDto updateWowCharacter(Long id, WowCharacterDto wowCharacterToUpdateDTO) {
        if (wowCharacterToUpdateDTO == null) {
            throw new RequiredObjectIsNull();
        }
        var entity = wowCharacterRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        updateNotNullFields(entity, wowCharacterToUpdateDTO);

        var dto = DTOMapper.parseObject(wowCharacterRepository.save(entity), WowCharacterDto.class);

        addLinks(dto);

        return dto;
    }

    @Override
    public void deleteWowCharacter(Long id) {
        if (!wowCharacterRepository.existsById(id)) {
            throw new NoSuchElementException();
        }
        wowCharacterRepository.deleteById(id);
    }

    private void addLinks(WowCharacterDto dto) {
        dto.add(linkTo(methodOn(WowCharacterController.class).getCharacterById(dto.getKey())).withSelfRel());
        dto.add(linkTo(methodOn(WowCharacterController.class)
                .putCharacter(dto.getKey(), null))
                .withRel("Update " + dto.getName() + " Information"));
        dto.add(linkTo(methodOn(WowCharacterController.class).askCharacter(null, null))
                    .withRel("Ask a Question to " + dto.getName()).expand());
    }

    private void updateNotNullFields(WowCharacter entity, WowCharacterDto dto) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getGender() != null) {
            entity.setGender(dto.getGender());
        }
        if (dto.getRace() != null) {
            entity.setRace(dto.getRace());
        }
        if (dto.getCharacterClass() != null) {
            entity.setCharacterClass(dto.getCharacterClass());
        }
        if (dto.getOccupation() != null) {
            entity.setOccupation(dto.getOccupation());
        }
        if (dto.getLore() != null) {
            entity.setLore(dto.getLore());
        }
    }
}
