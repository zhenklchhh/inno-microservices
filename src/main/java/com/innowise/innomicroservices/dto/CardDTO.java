package com.innowise.innomicroservices.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author Evgeniy Zaleshchenok
 */
@Getter
@Setter
public class CardDTO {
    @NotNull(message = "User id cannot be empty")
    private Long userId;

    @NotBlank(message = "Card number cannot be empty")
    @Size(min = 16, max = 19, message = "Card number must be between 16 and 19 digits")
    private String cardNumber;

    @NotBlank(message = "Card holder cannot be empty")
    private String holder;

    @NotNull(message = "Expiry date cannot be empty")
    @Future(message = "Expiry date must be in the future")
    LocalDate expiryDate;
}