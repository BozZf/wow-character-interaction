package me.dio.wow_character_interaction.service.impl;

import me.dio.wow_character_interaction.adapter.controller.exception.RequiredObjectIsNull;
import me.dio.wow_character_interaction.adapter.repository.UserRepository;
import me.dio.wow_character_interaction.data.dto.UserDto;
import me.dio.wow_character_interaction.domain.model.User;
import me.dio.wow_character_interaction.service.UserService;
import org.springframework.hateoas.Link;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.NoSuchElementException;


@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(UserDto dtoToCreate) {
        if (dtoToCreate == null) {
            throw new RequiredObjectIsNull();
        }
        String username = dtoToCreate.getUsername();
        requireText(username, "Username");
        requireText(dtoToCreate.getPassword(), "Password");
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        User entity = new User();
        entity.setUserName(username);
        entity.setFullName(dtoToCreate.getFullName());
        entity.setPassword(passwordEncoder.encode(dtoToCreate.getPassword()));
        setBooleansToTrue(entity);
        return toResponse(userRepository.save(entity));
    }

    @Transactional
    @Override
    public UserDto updateUserFullName(String username, String fullName) {
        requireText(fullName, "Full name");
        if (userRepository.updateFullName(username, fullName) == 0) {
            throw new NoSuchElementException();
        }
        return toResponse(userRepository.findByUsername(username));
    }

    @Transactional
    @Override
    public UserDto updateUserPassword(String username, String password) {
        requireText(password, "Password");
        if (userRepository.updatePassword(username, passwordEncoder.encode(password)) == 0) {
            throw new NoSuchElementException();
        }
        return toResponse(userRepository.findByUsername(username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    private void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }

    private UserDto toResponse(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/api/v1/users";
        dto.add(Link.of(baseUrl + "/create", "Create a new User"));
        dto.add(Link.of(baseUrl + "/update/:username/full-name", "Update a User full name"));
        dto.add(Link.of(baseUrl + "/update/:username/password", "Update a User password"));
        return dto;
    }

    private void setBooleansToTrue(User entity) {
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);
    }
}
