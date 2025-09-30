package com.innowise.innomicroservices.controller;

import com.innowise.innomicroservices.dto.UserDTO;
import com.innowise.innomicroservices.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO createUserRequestDTO) {
        UserDTO userResponseDto = userService.createUser(createUserRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        UserDTO userResponseDto = userService.getUserById(id);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/batch")
    public ResponseEntity<List<UserDTO>> getUsersById(@RequestParam List<Long> ids) {
        List<UserDTO> users = userService.getUsersByIds(ids);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
        UserDTO userResponseDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(userResponseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO updateUserRequestDto) {
        UserDTO userResponseDto = userService.updateUser(id, updateUserRequestDto);
        return ResponseEntity.ok(userResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
