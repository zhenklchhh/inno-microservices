package com.innowise.orderservice.model;

/**
 * @author Evgeniy Zaleshchenok
 */
public record OrderItemDto(Long orderId, Long itemId, Integer quantity) {
}
