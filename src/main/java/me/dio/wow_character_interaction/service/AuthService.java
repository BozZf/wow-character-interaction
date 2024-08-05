package me.dio.wow_character_interaction.service;

import me.dio.wow_character_interaction.data.dto.security.AccountCredentialsDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity signin(AccountCredentialsDto accountCredentialsDto);

    ResponseEntity refreshToken(String username, String refreshToken);
}
