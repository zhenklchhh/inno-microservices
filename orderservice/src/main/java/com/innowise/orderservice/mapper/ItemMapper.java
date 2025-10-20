package com.innowise.orderservice.mapper;

import com.innowise.orderservice.model.ItemDto;
import com.innowise.orderservice.model.entity.Item;
import org.mapstruct.Mapper;

/**
 * @author Evgeniy Zaleshchenok
 */
@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item toEntity(ItemDto itemDto);
    ItemDto toDto(Item item);
}
