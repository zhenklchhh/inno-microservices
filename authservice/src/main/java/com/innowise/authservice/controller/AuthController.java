package com.innowise.authservice.controller;

import com.innowise.authservice.jwt.TokenProvider;
import com.innowise.authservice.model.AccountDto;
import com.innowise.authservice.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AuthController {
    private final AccountService accountService;
    private final TokenProvider tokenProvider;

    @Autowired
    public AuthController(AccountService accountService, TokenProvider tokenProvider) {
        this.accountService = accountService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/account")
    public ResponseEntity<Void> createAccount(@Valid @RequestBody AccountDto accountDto) {
        accountService.createAccount(accountDto);
        return ResponseEntity.noContent().build();
    }
}
