package com.innowise.authservice.model;

import jakarta.validation.constraints.NotBlank;

/**
 * @author Evgeniy Zaleshchenok
 */
public record RefreshRequestDto(@NotBlank String refreshToken) {
}
