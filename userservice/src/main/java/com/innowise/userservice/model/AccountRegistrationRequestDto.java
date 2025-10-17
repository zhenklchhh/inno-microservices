package com.innowise.userservice.model;

import com.innowise.userservice.enums.UserRole;

/**
 * @author Evgeniy Zaleshchenok
 */
public record AccountRegistrationRequestDto(Long id, String login, String password, UserRole userRole) {
}
