package me.dio.wow_character_interaction.service.impl;

import me.dio.wow_character_interaction.adapter.controller.exception.RequiredObjectIsNull;
import me.dio.wow_character_interaction.data.dto.WowCharacterDto;
import me.dio.wow_character_interaction.adapter.repository.WowCharacterRepository;
import me.dio.wow_character_interaction.domain.model.WowCharacter;
import me.dio.wow_character_interaction.mapper.DTOMapper;
import me.dio.wow_character_interaction.service.WowCharacterService;
import org.springframework.hateoas.Link;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

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
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .build()
                .toUriString() + "/api/v1/character";

        URI findAllCharactersUri = UriComponentsBuilder
                .fromUriString(baseUrl + "/find-all")
                .build()
                .toUri();
        Link findAllCharactersLink = Link.of(findAllCharactersUri.toString(), "Finds all characters");
        dto.add(findAllCharactersLink);

        URI findByIdUri = UriComponentsBuilder
                .fromUriString(baseUrl + "/find/:id")
                .buildAndExpand()
                .toUri();
        Link findByIdLink = Link.of(findByIdUri.toString(), "Find a character");
        dto.add(findByIdLink);

        URI askCharacterUri = UriComponentsBuilder
                .fromUriString(baseUrl + "/ask/:id")
                .buildAndExpand()
                .toUri();
        Link askCharacterLink = Link.of(askCharacterUri.toString(), "Chat with a character");
        dto.add(askCharacterLink);

        if (checkAuthority()) {
            URI createCharacterUri = UriComponentsBuilder
                    .fromUriString(baseUrl + "/create")
                    .buildAndExpand()
                    .toUri();
            Link createCharacterLink = Link.of(createCharacterUri.toString(), "Add a character");
            dto.add(createCharacterLink);

            URI updateCharacterUri = UriComponentsBuilder
                    .fromUriString(baseUrl + "/update/:id")
                    .buildAndExpand()
                    .toUri();
            Link updateCharacterLink = Link.of(updateCharacterUri.toString(),
                    "Update a character");
            dto.add(updateCharacterLink);

            URI deleteCharacterUri = UriComponentsBuilder
                    .fromUriString(baseUrl + "/delete/:id")
                    .buildAndExpand()
                    .toUri();
            Link deleteCharacterLink = Link.of(deleteCharacterUri.toString(),
                    "Delete a character");
            dto.add(deleteCharacterLink);
        }
    }

    private Boolean checkAuthority() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Boolean hasRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> Set.of("ADMIN", "MANAGER").contains(role));

        return hasRoles;
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
