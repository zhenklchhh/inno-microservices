package com.innowise.orderservice.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author Evgeniy Zaleshchenok
 */
@Data
public class UserDto {
    private Long id;

    @NotBlank(message = "Name cannot be empty or contain only spaces")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Surname cannot be empty or contain only spaces")
    @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
    private String surname;

    @NotNull(message = "Birthdate must be provided")
    @Past(message = "Birthdate must be a date in the past")
    private LocalDate birthDate;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;
}