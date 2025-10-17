package com.innowise.orderservice.mapper;

import com.innowise.orderservice.model.OrderItemDto;
import com.innowise.orderservice.model.entity.OrderItem;
import org.mapstruct.Mapper;

/**
 * @author Evgeniy Zaleshchenok
 */
@Mapper(componentModel = "spring")
public interface OrderItemsMapper {
    OrderItem toEntity(OrderItemDto orderItemDto);
    OrderItemDto toDto(OrderItem orderItem);
}
