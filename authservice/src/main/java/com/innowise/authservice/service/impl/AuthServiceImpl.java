package com.innowise.authservice.service.impl;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.innowise.authservice.exception.InvalidJwtAuthenticationException;
import com.innowise.authservice.mapper.AccountMapper;
import com.innowise.authservice.model.JwtResponseDto;
import com.innowise.authservice.model.LoginRequestDto;
import com.innowise.authservice.model.entity.Account;
import com.innowise.authservice.security.JwtTokenProvider;
import com.innowise.authservice.service.AccountService;
import com.innowise.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * @author Evgeniy Zaleshchenok
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @Override
    public JwtResponseDto login(LoginRequestDto loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );
        Account account = accountMapper.toEntity(accountService.findAccountByLogin(loginRequest.username()));
        String accessToken = jwtTokenProvider.generateAccessToken(account);
        String refreshToken = jwtTokenProvider.generateRefreshToken(account);
        return new JwtResponseDto(accessToken, refreshToken);
    }

    @Override
    public JwtResponseDto refresh(String refreshToken) {
        try {
            String username = jwtTokenProvider.validateToken(refreshToken);
            Account account = accountMapper.toEntity(accountService.findAccountByLogin(username));
            String newAccessToken = jwtTokenProvider.generateAccessToken(account);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(account);
            return new JwtResponseDto(newAccessToken, newRefreshToken);
        } catch (JWTVerificationException ex) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT refresh token");
        }
    }
}
