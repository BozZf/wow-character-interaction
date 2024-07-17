package me.dio.wow_character_interaction.adapter.controller;

import me.dio.wow_character_interaction.application.AskCharacterUseCase;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/character/v1")
public class AskWowCharacterController {

    private final AskCharacterUseCase askCharacterUseCase;

    public AskWowCharacterController(AskCharacterUseCase askCharacterUseCase) {
        this.askCharacterUseCase = askCharacterUseCase;
    }

    @PostMapping("/{id}/ask")
    public AskCharacterResponse askCharacter(@PathVariable Long id, @RequestBody AskCharacterRequest request) {
        String answer = askCharacterUseCase.askCharacter(id, request.question());
        return new AskCharacterResponse(answer);
    }

    public record AskCharacterRequest(String question) {};
    public record AskCharacterResponse(String answer) {};
}
