package me.dio.wow_character_interaction.adapter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.dio.wow_character_interaction.application.AskCharacterUseCase;
import me.dio.wow_character_interaction.util.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/character")
@Tag(name = "WOW Characters Chat", description = "Endpoint to chat with a World Of Warcraft character.")
public class AskWowCharacterController {

    private final AskCharacterUseCase askCharacterUseCase;

    public AskWowCharacterController(AskCharacterUseCase askCharacterUseCase) {
        this.askCharacterUseCase = askCharacterUseCase;
    }

    @PostMapping(value = "/ask/{id}",
            consumes = {
                    MediaType.APPLICATION_JSON,
                    MediaType.APPLICATION_XML,
                    MediaType.APPLICATION_YAML},
            produces = {
                    MediaType.APPLICATION_JSON,
                    MediaType.APPLICATION_XML,
                    MediaType.APPLICATION_YAML})
    @Operation(summary = "Chat with a character",
            description = "Chat with a World Of Warcraft character.",
            tags = {"WOW Characters Chat"},
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(type = "string")),
                                    @Content(
                                            mediaType = "application/xml",
                                            schema = @Schema(type = "string")),
                                    @Content(
                                            mediaType = "application/x-yaml",
                                            schema = @Schema(type = "string"))
                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    public AskCharacterResponse askCharacter(@PathVariable Long id, @RequestBody AskCharacterRequest request) {
        String answer = askCharacterUseCase.askCharacter(id, request.question());
        return new AskCharacterResponse(answer);
    }

    public record AskCharacterRequest(String question) {};
    public record AskCharacterResponse(String answer) {};
}
