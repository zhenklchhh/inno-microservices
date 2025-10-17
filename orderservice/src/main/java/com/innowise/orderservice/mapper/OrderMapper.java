package com.innowise.orderservice.mapper;

import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.OrderDto;
import org.mapstruct.Mapper;

/**
 * @author Evgeniy Zaleshchenok
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toEntity(OrderDto orderDto);
    OrderDto toDto(Order order);
}
