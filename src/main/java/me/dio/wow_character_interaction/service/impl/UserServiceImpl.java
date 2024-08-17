package me.dio.wow_character_interaction.service.impl;

import me.dio.wow_character_interaction.adapter.controller.UserController;
import me.dio.wow_character_interaction.adapter.controller.exception.RequiredObjectIsNull;
import me.dio.wow_character_interaction.adapter.repository.UserRepository;
import me.dio.wow_character_interaction.data.dto.UserDto;
import me.dio.wow_character_interaction.domain.model.User;
import me.dio.wow_character_interaction.mapper.DTOMapper;
import me.dio.wow_character_interaction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public UserDto createUser(UserDto dtoToCreate) {
        if (dtoToCreate == null) {
            throw new RequiredObjectIsNull();
        }
        if (dtoToCreate.getUsername() == null ||
                userRepository.existsByUsername(dtoToCreate.getUsername())) {
            throw new IllegalArgumentException();
        }

        var entity = DTOMapper.parseObject(dtoToCreate, User.class);
        entity.setPassword(passwordEncoding(dtoToCreate.getPassword()));
        setBooleansToTrue(entity);

        var dto = DTOMapper.parseObject(userRepository.save(entity), UserDto.class);

        addLinks(dto);
        return dto;
    }

    @Override
    public UserDto updateUser(String username, UserDto dtoToUpdate) {
        if (dtoToUpdate == null) {
            throw new RequiredObjectIsNull();
        }

        var entityToUpdate = userRepository.findByUsername(username);
        if (entityToUpdate == null) {
            throw new NoSuchElementException("User with username " + username + " not found!");
        }

        var entityParsed = DTOMapper.parseObject(dtoToUpdate, User.class);

        updatePermittedFields(entityToUpdate, entityParsed);
        var dtoParsed = DTOMapper.parseObject(userRepository.save(entityToUpdate), UserDto.class);
        addLinks(dtoParsed);

        return dtoParsed;
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

    private void addLinks(UserDto dto) {
        dto.add(linkTo(methodOn(UserController.class).createUser(dto)).withRel("Create a new User"));
        dto.add(linkTo(methodOn(UserController.class).updateUser(dto.getUsername(), dto)).withRel("Update a User"));
    }

    private void setBooleansToTrue(User entity) {
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);
    }
}
