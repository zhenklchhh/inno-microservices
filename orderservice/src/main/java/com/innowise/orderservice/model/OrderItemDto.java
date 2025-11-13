package com.innowise.orderservice.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * @author Evgeniy Zaleshchenok
 */
public record OrderItemDto(
        @NotNull
        Long itemId,

        @Positive
        Integer quantity) {
}
