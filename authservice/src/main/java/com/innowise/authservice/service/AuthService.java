package com.innowise.authservice.service;

import com.innowise.authservice.model.JwtResponseDto;
import com.innowise.authservice.model.LoginRequestDto;

/**
 * Service interface for handling authentication processes.
 * This service is responsible for authenticating users and managing JWT lifecycles,
 * including token generation and refreshment.
 *
 * @author Evgeniy Zaleshchenok
 */
public interface AuthService {
    /**
     * Authenticates a user based on their login credentials and issues JWTs upon success.
     * It validates the provided login and password against the stored credentials.
     *
     * @param loginRequest A {@link LoginRequestDto} containing the user's login and raw password.
     * @return A {@link JwtResponseDto} containing a new access token and a refresh token.
     * @throws org.springframework.security.core.AuthenticationException if the credentials are invalid.
     */
    JwtResponseDto login(LoginRequestDto loginRequest);

    /**
     * Refreshes an expired access token using a valid refresh token.
     * This method validates the provided refresh token and, if valid, issues a new pair
     * of access and refresh tokens.
     *
     * @param refreshToken The valid refresh token provided by the client.
     * @return A {@link JwtResponseDto} containing a new access token and a new refresh token.
     * @throws com.innowise.authservice.exception.InvalidJwtAuthenticationException if the refresh token is invalid or expired.
     */
    JwtResponseDto refresh(String refreshToken);
}