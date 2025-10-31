package com.innowise.authservice.model;

import java.time.LocalDate;

/**
 * @author Evgeniy Zaleshchenok
 */
public record CreateUserRequestDto(
        String email,
        String name,
        String surname,
        LocalDate birthDate
) {
}
