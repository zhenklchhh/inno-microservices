package com.innowise.innomicroservices.controller;

import com.innowise.innomicroservices.dto.CreateUserRequestDto;
import com.innowise.innomicroservices.dto.UpdateUserRequestDto;
import com.innowise.innomicroservices.dto.UserResponseDto;
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
    public ResponseEntity<UserResponseDto> createUser(@RequestBody CreateUserRequestDto createUserRequestDto) {
        UserResponseDto userResponseDto = userService.createUser(createUserRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
        UserResponseDto userResponseDto = userService.getUserById(id);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/batch")
    public ResponseEntity<List<UserResponseDto>> getUsersById(@RequestParam List<Long> ids) {
        List<UserResponseDto> users = userService.getUsersByIds(ids);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<UserResponseDto> getUserByEmail(@RequestParam String email) {
        UserResponseDto userResponseDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(userResponseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequestDto updateUserRequestDto) {
        UserResponseDto userResponseDto = userService.updateUser(id, updateUserRequestDto);
        return ResponseEntity.ok(userResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
