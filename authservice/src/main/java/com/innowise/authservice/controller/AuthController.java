package com.innowise.authservice.controller;

import com.innowise.authservice.model.JwtResponseDto;
import com.innowise.authservice.model.LoginRequestDto;
import com.innowise.authservice.model.RefreshRequestDto;
import com.innowise.authservice.security.JwtTokenProvider;
import com.innowise.authservice.model.AccountDto;
import com.innowise.authservice.service.AccountService;
import com.innowise.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Evgeniy Zaleshchenok
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refresh(@RequestBody @Valid RefreshRequestDto refreshRequestDto) {
        return ResponseEntity.ok(authService.refresh(refreshRequestDto.refreshToken()));
    }
}
