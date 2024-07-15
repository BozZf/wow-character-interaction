package me.dio.wow_character_interaction.adapter.controller;

import me.dio.wow_character_interaction.data.dto.WowCharacterDTO;
import me.dio.wow_character_interaction.service.WowCharacterService;
import me.dio.wow_character_interaction.util.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/character")
public class WowCharacterController {

    private final WowCharacterService wowCharacterService;

    public WowCharacterController(WowCharacterService wowCharacterService) {
        this.wowCharacterService = wowCharacterService;
    }

    @GetMapping(produces = {
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_YAML})
    public List<WowCharacterDTO> getAllCharacters() {
        return wowCharacterService.findAllWowCharacters();
    }

    @GetMapping(value = "/{id}",
            produces = {
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML,
                MediaType.APPLICATION_YAML})
    public ResponseEntity<WowCharacterDTO> getCharacterById(@PathVariable Long id) {
        return ResponseEntity.ok(wowCharacterService.findWowCharacterById(id));
    }

    @PostMapping(consumes = {
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML,
                MediaType.APPLICATION_YAML},
            produces = {
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML,
                MediaType.APPLICATION_YAML})
    public ResponseEntity<WowCharacterDTO> postCharacter(@RequestBody WowCharacterDTO wowCharacterToCreate) {
        return ResponseEntity.ok(wowCharacterService.createWowCharacter(wowCharacterToCreate));
    }

    @PutMapping(value = "/{id}",
            consumes = {
                    MediaType.APPLICATION_JSON,
                    MediaType.APPLICATION_XML,
                    MediaType.APPLICATION_YAML},
            produces = {
                    MediaType.APPLICATION_JSON,
                    MediaType.APPLICATION_XML,
                    MediaType.APPLICATION_YAML})
    public ResponseEntity<WowCharacterDTO> putCharacter(@PathVariable Long id,
                                                     @RequestBody WowCharacterDTO wowCharacterToPut) {
        return ResponseEntity.ok(wowCharacterService.updateWowCharacter(id, wowCharacterToPut));
    }

    @DeleteMapping(value = "/{id}")
    public void deleteCharacter(@PathVariable Long id) {
        wowCharacterService.deleteWowCharacter(id);
    }
}
