package com.innowise.authservice.model;

import jakarta.validation.constraints.NotBlank;

/**
 * @author Evgeniy Zaleshchenok
 */
public record JwtResponseDto(
        @NotBlank String accessToken,
        @NotBlank String refreshToken
){
}
