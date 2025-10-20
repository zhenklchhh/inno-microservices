package com.innowise.orderservice.model;

import com.innowise.orderservice.model.entity.OrderStatus;
import lombok.Data;

import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@Data
public class OrderUpdateDto {
    private OrderStatus status;
    private List<OrderItemDto> orderItems;
}
