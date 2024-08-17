package me.dio.wow_character_interaction.adapter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.dio.wow_character_interaction.config.UsernameEnc;
import me.dio.wow_character_interaction.data.dto.security.AccountCredentialsDto;
import me.dio.wow_character_interaction.data.dto.security.TokenDto;
import me.dio.wow_character_interaction.service.impl.AuthServiceImpl;
import me.dio.wow_character_interaction.util.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication Endpoints", description = "Endpoints to authenticate users")
public class AuthController {

    @Autowired
    private UsernameEnc usernameEnc;

    @Autowired
    private AuthServiceImpl authService;

    public AuthController(UsernameEnc usernameEnc, AuthServiceImpl authService) {
        this.usernameEnc = usernameEnc;
        this.authService = authService;
    }

    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/signin",
            consumes = {
                    MediaType.APPLICATION_JSON,
                    MediaType.APPLICATION_XML,
                    MediaType.APPLICATION_YAML},
            produces = {
                    MediaType.APPLICATION_JSON,
                    MediaType.APPLICATION_XML,
                    MediaType.APPLICATION_YAML})
    @Operation(summary = "Authenticates a User",
            description = "Authenticates a User and returns a token",
            tags = {"Authentication Endpoints"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = TokenDto.class)),
                                    @Content(
                                            mediaType = "application/xml",
                                            schema = @Schema(implementation = TokenDto.class)),
                                    @Content(
                                            mediaType = "application/x-yaml",
                                            schema = @Schema(implementation = TokenDto.class))
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity signin(@RequestBody AccountCredentialsDto accountCredentialsDto) {
        if (checkParamsIsNotNull(accountCredentialsDto)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }
        var token = authService.signin(accountCredentialsDto);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }

        return token;
    }

    @SuppressWarnings("rawtypes")
    @PutMapping(value = "/refresh/{username}",
            produces = {
                    MediaType.APPLICATION_JSON,
                    MediaType.APPLICATION_XML,
                    MediaType.APPLICATION_YAML})
    @Operation(summary = "Refresh token",
            description = "Refresh token for a authenticated User and returns a token",
            tags = {"Authentication Endpoints"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = TokenDto.class)),
                                    @Content(
                                            mediaType = "application/xml",
                                            schema = @Schema(implementation = TokenDto.class)),
                                    @Content(
                                            mediaType = "application/x-yaml",
                                            schema = @Schema(implementation = TokenDto.class))
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity refreshToken(@PathVariable("username") String encryptedUsername,
                                       @RequestHeader("Authorization") String refreshToken) {
        if (checkParamsIsNotNull(encryptedUsername, refreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }
        String decryptedUsername = usernameEnc.decrypt(encryptedUsername);
        var token = authService.refreshToken(decryptedUsername, refreshToken);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }

        return token;
    }

    private boolean checkParamsIsNotNull(AccountCredentialsDto accountCredentialsDto) {
        return accountCredentialsDto == null ||
                accountCredentialsDto.getUsername() == null ||
                accountCredentialsDto.getUsername().isBlank() ||
                accountCredentialsDto.getPassword() == null ||
                accountCredentialsDto.getPassword().isBlank();
    }

    private boolean checkParamsIsNotNull(String username, String refreshToken) {
        return username == null || username.isBlank() || refreshToken == null || refreshToken.isBlank();
    }
}
