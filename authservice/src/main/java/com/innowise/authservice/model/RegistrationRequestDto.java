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
        @Size(min = 8, max = 128)
        String password,

        @NotBlank
        @Size(min = 3, max = 50)
        String name,

        @NotBlank
        @Size(min = 3, max = 50)
        String surname,

        @Past
        @NotNull
        LocalDate birthDate,

        @NotNull
        UserRole userRole
) {
}
