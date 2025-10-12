package com.innowise.userservice.model;

/**
 * @author Evgeniy Zaleshchenok
 */
public record AccountRegistrationRequestDto(Long id, String login, String password, String role) {
}
