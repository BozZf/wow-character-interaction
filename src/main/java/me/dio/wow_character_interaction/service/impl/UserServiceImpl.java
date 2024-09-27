package me.dio.wow_character_interaction.service.impl;

import me.dio.wow_character_interaction.adapter.controller.exception.RequiredObjectIsNull;
import me.dio.wow_character_interaction.adapter.repository.UserRepository;
import me.dio.wow_character_interaction.data.dto.UserDto;
import me.dio.wow_character_interaction.domain.model.User;
import me.dio.wow_character_interaction.mapper.DTOMapper;
import me.dio.wow_character_interaction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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

    @Transactional
    @Override
    public UserDto updateUserFullName(String username, String fullName) {
        if (fullName == null) {
            throw new RequiredObjectIsNull();
        }

        if (!userRepository.existsByUsername(username)) {
            throw new NoSuchElementException("User with username " + username + " not found!");
        }

        userRepository.updateFullName(username, fullName);
        var dtoParsed = DTOMapper.parseObject(userRepository.findByUsername(username), UserDto.class);
        addLinks(dtoParsed);

        return dtoParsed;
    }

    @Transactional
    @Override
    public UserDto updateUserPassword(String username, String password) {
        if (password == null) {
            throw new RequiredObjectIsNull();
        }

        if (!userRepository.existsByUsername(username)) {
            throw new NoSuchElementException("User with username " + username + " not found!");
        }

        String encodedPassword = passwordEncoding(password);
        userRepository.updatePassword(username, encodedPassword);

        var dtoParsed = DTOMapper.parseObject(userRepository.findByUsername(username), UserDto.class);
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

    private String passwordEncoding(String password) {
        return passwordEncoder.encode(password);
    }

    private void addLinks(UserDto dto) {

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .build()
                .toUriString() + "/api/v1/users";

        URI createUserUri = UriComponentsBuilder
                .fromUriString(baseUrl + "/create")
                .build()
                .toUri();
        Link createUserLink = Link.of(createUserUri.toString(), "Create a new User");
        dto.add(createUserLink);

        URI updateFullNameUri = UriComponentsBuilder
                .fromUriString(baseUrl + "/update/:username/full-name")
                .buildAndExpand()
                .toUri();
        Link updateFullNameLink = Link.of(updateFullNameUri.toString(), "Update a User full name");
        dto.add(updateFullNameLink);

        URI updatePasswordUri = UriComponentsBuilder
                .fromUriString(baseUrl + "/update/:username/password")
                .buildAndExpand()
                .toUri();
        Link updatePasswordLink = Link.of(updatePasswordUri.toString(), "Update a User password");
        dto.add(updatePasswordLink);
    }

    private void setBooleansToTrue(User entity) {
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);
    }
}
