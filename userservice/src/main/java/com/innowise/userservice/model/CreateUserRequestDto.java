package com.innowise.userservice.model;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * @author Evgeniy Zaleshchenok
 */
public record CreateUserRequestDto(
        @NotBlank
        String name,

        @NotBlank
        String surname,

        @NotNull
        @Past
        LocalDate birthDate,

        @NotBlank
        @Email(message = "Invalid email format")
        String email,

        @NotBlank
        @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
        String password
) {
}
