package me.dio.wow_character_interaction.adapter.controller;

import me.dio.wow_character_interaction.domain.model.WowCharacter;
import me.dio.wow_character_interaction.service.WowCharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/character")
public class WowCharacterController {

    private final WowCharacterService wowCharacterService;

    public WowCharacterController(WowCharacterService wowCharacterService) {
        this.wowCharacterService = wowCharacterService;
    }

    @GetMapping
    public List<WowCharacter> getAllCharacters() {
        return wowCharacterService.findAllWowCharacters();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WowCharacter> getCharacterById(@PathVariable Long id) {
        return ResponseEntity.ok(wowCharacterService.findWowCharacterById(id));
    }

    @PostMapping
    public ResponseEntity<WowCharacter> postCharacter(@RequestBody WowCharacter wowCharacterToCreate) {
        return ResponseEntity.ok(wowCharacterService.createWowCharacter(wowCharacterToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WowCharacter> putCharacter(@PathVariable Long id,
                                                     @RequestBody WowCharacter wowCharacterToPut) {
        return ResponseEntity.ok(wowCharacterService.updateWowCharacter(id, wowCharacterToPut));
    }
}
