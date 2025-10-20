package com.innowise.orderservice.model;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
public record OrderDto (
        @NotNull
        Long userId,

        @NotEmpty
        @Valid
        List<OrderItemDto> orderItems
        ){
}
