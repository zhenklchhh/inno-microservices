package com.innowise.authservice.model;

import com.innowise.authservice.enums.UserRole;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * @author Evgeniy Zaleshchenok
 */
public record RegistrationRequestDto(
        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 2, max = 6)
        String password,

        @NotBlank
        @Size(min = 2, max = 6)
        String name,

        @NotBlank
        @Size(min = 2, max = 6)
        String surname,

        @Past
        @NotNull
        LocalDate birthDate,

        @NotNull
        UserRole userRole
) {
}
