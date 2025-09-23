package com.innowise.innomicroservices.mapper;

import com.innowise.innomicroservices.dto.CardResponseDto;
import com.innowise.innomicroservices.dto.CreateCardRequestDto;
import com.innowise.innomicroservices.model.Card;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author Evgeniy Zaleshchenok
 */
@Mapper(componentModel = "spring")
public interface CardMapper {
    Card toEntity(CreateCardRequestDto createCardRequestDto);
    CardResponseDto toResponseDto(Card card);
}
