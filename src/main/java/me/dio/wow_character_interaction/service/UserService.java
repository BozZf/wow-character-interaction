package me.dio.wow_character_interaction.service;

import me.dio.wow_character_interaction.data.dto.UserDto;
import me.dio.wow_character_interaction.domain.model.User;

public interface UserService {

    UserDto createUser(UserDto dtoToCreate);

    UserDto updateUserFullName(String username, String fullName);

    UserDto updateUserPassword(String username, String password);

    void deleteUser(Long id);

    void updateUserAccountNonExpired(Long id, Boolean state);

    void updateUserAccountNonLocked(Long id, Boolean state);

    void updateUserCredentialsNonExpired(Long id, Boolean state);

    void updateUserEnabled(Long id, Boolean state);
}
