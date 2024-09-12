package me.dio.wow_character_interaction.adapter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.dio.wow_character_interaction.application.AskCharacterUseCase;
import me.dio.wow_character_interaction.data.dto.AskCharacterRequestDto;
import me.dio.wow_character_interaction.data.dto.AskCharacterResponseDto;
import me.dio.wow_character_interaction.data.dto.WowCharacterDto;
import me.dio.wow_character_interaction.service.WowCharacterService;
import me.dio.wow_character_interaction.util.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/character")
@Tag(name = "WOW Characters", description = "Endpoints for managing World Of Warcraft characters")
public class WowCharacterController {

    private final WowCharacterService wowCharacterService;

    private final AskCharacterUseCase askCharacterUseCase;

    public WowCharacterController(WowCharacterService wowCharacterService, AskCharacterUseCase askCharacterUseCase) {
        this.wowCharacterService = wowCharacterService;
        this.askCharacterUseCase = askCharacterUseCase;
    }

    @GetMapping(value = "/find-all", produces = {
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_YAML})
    @Operation(summary = "Finds all characters",
            description = "Return a list of all characters.",
            tags = {"WOW Characters"},
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                @ApiResponse(description = "Success", responseCode = "200",
                    content = {
                        @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = WowCharacterDto.class))),
                        @Content(
                            mediaType = "application/xml",
                            array = @ArraySchema(schema = @Schema(implementation = WowCharacterDto.class))),
                        @Content(
                            mediaType = "application/x-yaml",
                            array = @ArraySchema(schema = @Schema(implementation = WowCharacterDto.class)))
                }),
                @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    public List<WowCharacterDto> getAllCharacters() {
        return wowCharacterService.findAllWowCharacters();
    }

    @GetMapping(value = "/find/{id}",
            produces = {
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML,
                MediaType.APPLICATION_YAML})
    @Operation(summary = "Find a character",
            description = "Find and return a character passing his ID.",
            tags = {"WOW Characters"},
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = WowCharacterDto.class)),
                                    @Content(
                                            mediaType = "application/xml",
                                            schema = @Schema(implementation = WowCharacterDto.class)),
                                    @Content(
                                            mediaType = "application/x-yaml",
                                            schema = @Schema(implementation = WowCharacterDto.class))
                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<WowCharacterDto> getCharacterById(@PathVariable Long id) {
        return ResponseEntity.ok(wowCharacterService.findWowCharacterById(id));
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
    public AskCharacterResponseDto askCharacter(@PathVariable Long id, @RequestBody AskCharacterRequestDto request) {
        String answer = askCharacterUseCase.askCharacter(id, request.getQuestion());
        return new AskCharacterResponseDto(answer);
    }

    @PostMapping(value = "/create", consumes = {
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML,
                MediaType.APPLICATION_YAML},
            produces = {
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML,
                MediaType.APPLICATION_YAML})
    @Operation(summary = "Add a character",
            description = "Add a character to data base by passing a representation in a JSON, XML or YAML format.",
            tags = {"WOW Characters"},
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = WowCharacterDto.class)),
                                    @Content(
                                            mediaType = "application/xml",
                                            schema = @Schema(implementation = WowCharacterDto.class)),
                                    @Content(
                                            mediaType = "application/x-yaml",
                                            schema = @Schema(implementation = WowCharacterDto.class))
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<WowCharacterDto> postCharacter(@RequestBody WowCharacterDto wowCharacterToCreate) {
        return ResponseEntity.ok(wowCharacterService.createWowCharacter(wowCharacterToCreate));
    }

    @PutMapping(value = "/update/{id}",
            consumes = {
                    MediaType.APPLICATION_JSON,
                    MediaType.APPLICATION_XML,
                    MediaType.APPLICATION_YAML},
            produces = {
                    MediaType.APPLICATION_JSON,
                    MediaType.APPLICATION_XML,
                    MediaType.APPLICATION_YAML})
    @Operation(summary = "Update a character",
            description = "Update a character data by passing a representation in a JSON, XML or YAML format.",
            tags = {"WOW Characters"},
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = WowCharacterDto.class)),
                                    @Content(
                                            mediaType = "application/xml",
                                            schema = @Schema(implementation = WowCharacterDto.class)),
                                    @Content(
                                            mediaType = "application/x-yaml",
                                            schema = @Schema(implementation = WowCharacterDto.class))
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<WowCharacterDto> putCharacter(@PathVariable Long id,
                                                        @RequestBody WowCharacterDto wowCharacterToPut) {
        return ResponseEntity.ok(wowCharacterService.updateWowCharacter(id, wowCharacterToPut));
    }

    @DeleteMapping(value = "/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a character",
            description = "Delete a character from data base passing his ID.",
            tags = {"WOW Characters"},
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    public void deleteCharacter(@PathVariable Long id) {
        wowCharacterService.deleteWowCharacter(id);
    }

}
