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
    public UserDto updateUserFullName(String username, String fullName) {
        if (fullName == null) {
            throw new RequiredObjectIsNull();
        }

        if (!userRepository.existsByUsername(username)) {
            throw new NoSuchElementException("User with username " + username + " not found!");
        }

        var dtoParsed = DTOMapper.parseObject(userRepository.updateFullName(username, fullName), UserDto.class);
        addLinks(dtoParsed);

        return dtoParsed;
    }

    @Override
    public UserDto updateUserPassword(String username, String password) {
        if (password == null) {
            throw new RequiredObjectIsNull();
        }

        if (!userRepository.existsByUsername(username)) {
            throw new NoSuchElementException("User with username " + username + " not found!");
        }

        String encodedPassword = passwordEncoding(password);

        var dtoParsed = DTOMapper.parseObject(userRepository.updatePassword(username, encodedPassword),
                UserDto.class);
        addLinks(dtoParsed);

        return dtoParsed;
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException();
        }
        userRepository.deleteById(id);
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

    @Override
    public void updateUserAccountNonExpired(Long id, Boolean state) {
        userRepository.updateAccountNonExpired(id, state);
    }

    @Override
    public void updateUserAccountNonLocked(Long id, Boolean state) {
        userRepository.updateAccountNonLocked(id, state);
    }

    @Override
    public void updateUserCredentialsNonExpired(Long id, Boolean state) {
        userRepository.updateCredentialsNonExpired(id, state);
    }

    @Override
    public void updateUserEnabled(Long id, Boolean state) {
        userRepository.updateEnabled(id, state);
    }

    private String passwordEncoding(String password) {
        return passwordEncoder.encode(password);
    }

    private void addLinks(UserDto dto) {
        dto.add(linkTo(methodOn(UserController.class).createUser(null)).withRel("Create a new User"));
        dto.add(linkTo(methodOn(UserController.class).patchUserFullName(null, null))
                                                        .withRel("Update a User full name"));
        dto.add(linkTo(methodOn(UserController.class).patchUserPassword(null, null))
                .withRel("Update a User password"));
    }

    private void setBooleansToTrue(User entity) {
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);
    }
}
