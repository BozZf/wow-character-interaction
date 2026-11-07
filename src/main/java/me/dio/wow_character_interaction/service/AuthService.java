package me.dio.wow_character_interaction.service;

import me.dio.wow_character_interaction.data.dto.security.AccountCredentialsDto;
import me.dio.wow_character_interaction.data.dto.security.TokenDto;

public interface AuthService {

    TokenDto signin(AccountCredentialsDto credentials);

    TokenDto refreshToken(String username, String refreshToken);
}
