package me.dio.wow_character_interaction.adapter.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.dio.wow_character_interaction.configs.UsernameEnc;
import me.dio.wow_character_interaction.data.dto.UserDto;
import me.dio.wow_character_interaction.service.impl.UserServiceImpl;
import me.dio.wow_character_interaction.util.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Endpoints", description = "Endpoints manage users")
public class UserController {

    @Autowired
    private UsernameEnc usernameEnc;

    @Autowired
    private UserServiceImpl userService;

    public UserController(UsernameEnc usernameEnc, UserServiceImpl userService) {
        this.usernameEnc = usernameEnc;
        this.userService = userService;
    }

    @PostMapping(value = "/create",
        consumes = {
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_YAML},
        produces = {
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_YAML})
    @Operation(summary = "Create a new User",
            description = "Create a new User on data base passing a representation in a JSON, XML or YAML format.",
            tags = {"User Endpoints"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = UserDto.class)),
                                    @Content(
                                            mediaType = "application/xml",
                                            schema = @Schema(implementation = UserDto.class)),
                                    @Content(
                                            mediaType = "application/x-yaml",
                                            schema = @Schema(implementation = UserDto.class))
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto dtoToCreate) {
        return ResponseEntity.ok(userService.createUser(dtoToCreate));
    }

    @PatchMapping(value = "/update/{username}/full-name",
        consumes = {
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_YAML},
        produces = {
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_YAML})
    @Operation(summary = "Update a User full name",
            description = "Update a User full name in a JSON, XML or YAML format.",
            tags = {"User Endpoints"},
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = UserDto.class)),
                                    @Content(
                                            mediaType = "application/xml",
                                            schema = @Schema(implementation = UserDto.class)),
                                    @Content(
                                            mediaType = "application/x-yaml",
                                            schema = @Schema(implementation = UserDto.class))
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<UserDto> patchUserFullName(@PathVariable("username") String encryptedUsername,
                                              @RequestBody String fullName) {
        String decryptedUsername = usernameEnc.decrypt(encryptedUsername);
        return ResponseEntity.ok(userService.updateUserFullName(decryptedUsername, fullName));
    }

    @PatchMapping(value = "/update/{username}/password",
            consumes = {
                    MediaType.APPLICATION_JSON,
                    MediaType.APPLICATION_XML,
                    MediaType.APPLICATION_YAML},
            produces = {
                    MediaType.APPLICATION_JSON,
                    MediaType.APPLICATION_XML,
                    MediaType.APPLICATION_YAML})
    @Operation(summary = "Update a User password",
            description = "Update a User password in a JSON, XML or YAML format.",
            tags = {"User Endpoints"},
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = UserDto.class)),
                                    @Content(
                                            mediaType = "application/xml",
                                            schema = @Schema(implementation = UserDto.class)),
                                    @Content(
                                            mediaType = "application/x-yaml",
                                            schema = @Schema(implementation = UserDto.class))
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<UserDto> patchUserPassword(@PathVariable("username") String encryptedUsername,
                                                     @RequestBody String password) {
        String decryptedUsername = usernameEnc.decrypt(encryptedUsername);
        return ResponseEntity.ok(userService.updateUserPassword(decryptedUsername, password));
    }
}
