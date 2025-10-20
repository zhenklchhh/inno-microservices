package com.innowise.orderservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * @author Evgeniy Zaleshchenok
 */
public record ItemDto(
        @NotBlank
        @Size(min = 2, max = 50)
        String name,

        @Positive
        Integer price) {
}
