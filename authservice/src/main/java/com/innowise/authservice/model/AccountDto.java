package com.innowise.authservice.model;

import com.innowise.authservice.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author Evgeniy Zaleshchenok
 */
public record AccountDto(
        @NotNull
        Long id,
        @NotBlank
        String login,
        @NotBlank
        String password,
        @NotNull
        UserRole userRole) {
}
