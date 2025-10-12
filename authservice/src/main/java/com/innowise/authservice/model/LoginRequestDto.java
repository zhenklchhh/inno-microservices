package com.innowise.authservice.model;

import jakarta.validation.constraints.NotBlank;

/**
 * @author Evgeniy Zaleshchenok
 */
public record LoginRequestDto (
        @NotBlank
        String username,
        @NotBlank
        String password
){
}
