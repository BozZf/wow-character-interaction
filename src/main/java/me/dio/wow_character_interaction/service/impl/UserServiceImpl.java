package me.dio.wow_character_interaction.service.impl;

import me.dio.wow_character_interaction.adapter.controller.exception.RequiredObjectIsNull;
import me.dio.wow_character_interaction.adapter.repository.UserRepository;
import me.dio.wow_character_interaction.domain.model.User;
import me.dio.wow_character_interaction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User userToCreate) {
        if (userToCreate == null) {
            throw new RequiredObjectIsNull();
        }
        if (userToCreate.getUserName() != null &&
                userRepository.existsByUsername(userToCreate.getUserName())) {
            throw new IllegalArgumentException();
        }
        userToCreate.setPassword(passwordEncoding(userToCreate.getPassword()));
        return userRepository.save(userToCreate);
    }

    @Override
    public User updateUser(String username, User userToUpdate) {
        if (userToUpdate == null) {
            throw new RequiredObjectIsNull();
        }

        var entity = userRepository.findByUsername(username);
        if (entity == null) {
            throw new NoSuchElementException("User with username " + username + " not found!");
        }

        updatePermittedFields(entity, userToUpdate);

        return userRepository.save(entity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username);
        if (user != null) {
            return user;
        } else {
            throw new UsernameNotFoundException("Username " + username + " not found!");
        }
    }

    private void updatePermittedFields(User entity, User userToUpdate) {
        if (userToUpdate.getFullName() != null) {
            entity.setFullName(userToUpdate.getFullName());
        }
        if (userToUpdate.getPassword() != null) {
            entity.setPassword(passwordEncoding(userToUpdate.getPassword()));
        }
    }

    private String passwordEncoding(String password) {
        return passwordEncoder.encode(password);
    }
}
