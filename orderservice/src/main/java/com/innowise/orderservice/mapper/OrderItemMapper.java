package com.innowise.orderservice.mapper;

import com.innowise.orderservice.model.OrderItemDto;
import com.innowise.orderservice.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Evgeniy Zaleshchenok
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItem toEntity(OrderItemDto orderItemDto);

    @Mapping(source = "item.id", target = "itemId")
    OrderItemDto toDto(OrderItem orderItem);
}
