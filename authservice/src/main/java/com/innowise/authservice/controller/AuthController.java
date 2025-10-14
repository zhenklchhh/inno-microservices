package com.innowise.authservice.controller;

import com.innowise.authservice.model.AccountDto;
import com.innowise.authservice.model.JwtResponseDto;
import com.innowise.authservice.model.LoginRequestDto;
import com.innowise.authservice.model.RefreshRequestDto;
import com.innowise.authservice.service.AccountService;
import com.innowise.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Evgeniy Zaleshchenok
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AccountService accountService;

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
    public ResponseEntity<Void> createAccount(@Valid @RequestBody AccountDto accountDto) {
        accountService.createAccount(accountDto);
        return ResponseEntity.ok().build();
    }
}
