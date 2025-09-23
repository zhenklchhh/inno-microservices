package com.innowise.innomicroservices.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * @author Evgeniy Zaleshchenok
 */
public record CreateCardRequestDto(
        @NotNull(message = "User id cannot be empty")
        Long userId,

        @NotBlank(message = "Card number cannot be empty")
        @Size(min = 16, max = 19, message = "Card number must be between 16 and 19 digits")
        String cardNumber,

        @NotBlank(message = "Card holder cannot be empty")
        String holder,

        @NotNull(message = "Expiry date cannot be empty")
        @Future(message = "Expiry date must be in the future")
        LocalDate expiryDate
) {
}