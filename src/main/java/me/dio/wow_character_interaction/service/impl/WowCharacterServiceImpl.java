package me.dio.wow_character_interaction.service.impl;

import me.dio.wow_character_interaction.adapter.controller.AskWowCharacterController;
import me.dio.wow_character_interaction.adapter.controller.WowCharacterController;
import me.dio.wow_character_interaction.data.dto.WowCharacterDTO;
import me.dio.wow_character_interaction.adapter.repository.WowCharacterRepository;
import me.dio.wow_character_interaction.domain.model.WowCharacter;
import me.dio.wow_character_interaction.mapper.DTOMapper;
import me.dio.wow_character_interaction.service.WowCharacterService;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

@Service
public class WowCharacterServiceImpl implements WowCharacterService {

    private final WowCharacterRepository wowCharacterRepository;

    public WowCharacterServiceImpl(WowCharacterRepository wowCharacterRepository) {
        this.wowCharacterRepository = wowCharacterRepository;
    }

    @Override
    public List<WowCharacterDTO> findAllWowCharacters() {
        var characters = DTOMapper.parseListObjects(wowCharacterRepository.findAll(), WowCharacterDTO.class);

        Consumer<WowCharacterDTO> addLinks = c -> {
            c.add(linkTo(methodOn(WowCharacterController.class).getCharacterById(c.getKey())).withSelfRel());
            c.add(linkTo(methodOn(WowCharacterController.class)
                    .putCharacter(c.getKey(), null))
                    .withRel("Update " + c.getName() + " Information"));
            c.add(linkTo(AskWowCharacterController.class).withRel("Ask a Question to " + c.getName()));};

        characters.forEach(addLinks);

        return characters;
    }

    @Override
    public WowCharacterDTO findWowCharacterById(Long id) {
        var entity = wowCharacterRepository.findById(id).orElseThrow(NoSuchElementException::new);
        WowCharacterDTO dto = DTOMapper.parseObject(entity, WowCharacterDTO.class);
        dto.add(linkTo(methodOn(WowCharacterController.class).getCharacterById(id)).withSelfRel());
        dto.add(linkTo(methodOn(WowCharacterController.class)
                .putCharacter(id, null))
                .withRel("Update " + dto.getName() + " Information"));
        dto.add(linkTo(AskWowCharacterController.class).withRel("Ask a Question to " + dto.getName()));

        return dto;
    }

    @Override
    public WowCharacterDTO createWowCharacter(WowCharacterDTO wowCharacterToCreate) {
        if (wowCharacterToCreate.getName() != null &&
                wowCharacterRepository.existsByName(wowCharacterToCreate.getName())) {
            throw new IllegalArgumentException();
        }

        var entity = DTOMapper.parseObject(wowCharacterToCreate, WowCharacter.class);
        var dto = DTOMapper.parseObject(wowCharacterRepository.save(entity), WowCharacterDTO.class);

        dto.add(linkTo(methodOn(WowCharacterController.class).getCharacterById(dto.getKey())).withSelfRel());
        dto.add(linkTo(methodOn(WowCharacterController.class)
                .putCharacter(dto.getKey(), null))
                .withRel("Update " + dto.getName() + " Information"));
        dto.add(linkTo(AskWowCharacterController.class).withRel("Ask a Question to " + dto.getName()));

        return dto;
    }

    @Override
    public WowCharacterDTO updateWowCharacter(Long id, WowCharacterDTO wowCharacterToUpdate) {
        if (id != null && !wowCharacterRepository.existsById(id)) {
            throw new NoSuchElementException();
        }
        var entity = DTOMapper.parseObject(wowCharacterToUpdate, WowCharacter.class);
        var dto = DTOMapper.parseObject(wowCharacterRepository.save(entity), WowCharacterDTO.class);

        dto.add(linkTo(methodOn(WowCharacterController.class).getCharacterById(dto.getKey())).withSelfRel());
        dto.add(linkTo(methodOn(WowCharacterController.class)
                .putCharacter(dto.getKey(), null))
                .withRel("Update " + dto.getName() + " Information"));
        dto.add(linkTo(AskWowCharacterController.class).withRel("Ask a Question to " + dto.getName()));

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
