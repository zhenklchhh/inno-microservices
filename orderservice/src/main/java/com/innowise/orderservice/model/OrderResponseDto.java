package com.innowise.orderservice.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@Data
public class OrderResponseDto {
    private Long id;
    private String status;
    private LocalDate creationDate;
    private UserDto user;
    private List<OrderItemDto> orderItems;
}
