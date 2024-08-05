package me.dio.wow_character_interaction.adapter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.dio.wow_character_interaction.data.dto.security.AccountCredentialsDto;
import me.dio.wow_character_interaction.service.AuthService;
import me.dio.wow_character_interaction.service.impl.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication Endpoints")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    AuthServiceImpl authService;

    @SuppressWarnings("rawtypes")
    @Operation(summary = "Authenticates a user and returns a token")
    @PostMapping(value = "/signin")
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
    @Operation(summary = "Refresh token for a authenticated user and returns a token")
    @PutMapping(value = "/refresh/{username}")
    public ResponseEntity refreshToken(@PathVariable("username") String username,
                                       @RequestHeader("Authorization") String refreshToken) {
        if (checkParamsIsNotNull(username, refreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }
        var token = authService.refreshToken(username, refreshToken);
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
