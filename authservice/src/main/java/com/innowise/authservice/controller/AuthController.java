package com.innowise.authservice.controller;

import com.innowise.authservice.model.*;
import com.innowise.authservice.service.AccountService;
import com.innowise.authservice.service.AuthService;
import com.innowise.authservice.service.impl.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @author Evgeniy Zaleshchenok
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RegistrationService registrationService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refresh(@RequestBody @Valid RefreshRequestDto refreshRequestDto) {
        return ResponseEntity.ok(authService.refresh(refreshRequestDto.refreshToken()));
    }

    @PostMapping("/validate")
    public ResponseEntity<Void> validate() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserDto> createAccount(@Valid @RequestBody RegistrationRequestDto registrationRequestDto,
                                       @RequestHeader("Authentification") String authToken) {
        return registrationService.registerUser(registrationRequestDto, authToken);
    }
}
