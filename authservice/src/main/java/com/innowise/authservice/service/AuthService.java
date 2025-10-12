package com.innowise.authservice.service;

import com.innowise.authservice.model.JwtResponseDto;
import com.innowise.authservice.model.LoginRequestDto;

/**
 * @author Evgeniy Zaleshchenok
 */
public interface AuthService {
    JwtResponseDto login(LoginRequestDto loginRequest);
    JwtResponseDto refresh(String refreshToken);
}
