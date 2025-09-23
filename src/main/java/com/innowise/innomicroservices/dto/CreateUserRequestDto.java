package com.innowise.innomicroservices.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author Evgeniy Zaleshchenok
 */
public record CreateUserRequestDto(

        @NotBlank(message = "Name cannot be empty")
        @Size(min = 2, max = 50)
        String name,

        @NotBlank(message = "Surname cannot be empty")
        @Size(min = 2, max = 50)
        String surname,

        @NotNull(message = "Birthdate cannot be empty")
        @Past(message = "Birthdate must be before current date")
        LocalDate birthDate,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email
) {
}