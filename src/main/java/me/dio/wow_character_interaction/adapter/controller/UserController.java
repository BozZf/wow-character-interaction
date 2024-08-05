package me.dio.wow_character_interaction.adapter.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.dio.wow_character_interaction.domain.model.User;
import me.dio.wow_character_interaction.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Endpoints")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Operation(summary = "Create a new user")
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User userToCreate) {
        return ResponseEntity.ok(userService.createUser(userToCreate));
    }
    
    @Operation(summary = "Update user available information")
    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody User userToCreate) {
        return ResponseEntity.ok(userService.updateUser(userToCreate.getUserName(), userToCreate));
    }
}
