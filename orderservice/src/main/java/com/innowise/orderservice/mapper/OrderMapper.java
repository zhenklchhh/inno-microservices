package com.innowise.orderservice.mapper;

import com.innowise.orderservice.model.OrderResponseDto;
import com.innowise.orderservice.model.UserDto;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * @author Evgeniy Zaleshchenok
 */
@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(target="id", ignore = true)
    @Mapping(target="creationDate", ignore = true)
    @Mapping(target="status", ignore = true)
    @Mapping(target="orderItems", source = "orderItems")
    Order toEntity(OrderDto orderDto);

    OrderResponseDto toResponseDto(Order order);

    @Mapping(target = "id", source = "order.id")
    @Mapping(source = "order.status", target = "status")
    @Mapping(source = "order.creationDate", target = "creationDate")
    @Mapping(source = "order.orderItems", target = "orderItems")
    @Mapping(target = "user", source = "userDto")
    OrderResponseDto toResponseDto(Order order, UserDto userDto);
}
