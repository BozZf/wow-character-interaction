package me.dio.wow_character_interaction.service;

import me.dio.wow_character_interaction.data.dto.UserDto;
import me.dio.wow_character_interaction.domain.model.User;

public interface UserService {

    UserDto createUser(UserDto dtoToCreate);

    UserDto updateUserFullName(String username, String fullName);

    UserDto updateUserPassword(String username, String password);
}
