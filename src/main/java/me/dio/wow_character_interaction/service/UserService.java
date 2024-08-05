package me.dio.wow_character_interaction.service;

import me.dio.wow_character_interaction.domain.model.User;

public interface UserService {

    User createUser(User userToCreate);

    User updateUser(String username, User userToUpdate);
}
