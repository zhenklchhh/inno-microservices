package com.innowise.authservice.model;

import java.time.LocalDate;

/**
 * @author Evgeniy Zaleshchenok
 */
public record UserDto(
        Long id,
        String email,
        String name,
        String surname,
        LocalDate birthDate
) {
}
