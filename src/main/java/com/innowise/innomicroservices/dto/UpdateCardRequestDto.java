package com.innowise.innomicroservices.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author Evgeniy Zaleshchenok
 */
@Getter
@Setter
public class UpdateCardRequestDto {
        @Size(min = 16, max = 19, message = "Card number must be between 16 and 19 digits")
        private String cardNumber;

        private String holder;

        @Future(message = "Expiry date must be in the future")
        private LocalDate expiryDate;
}
