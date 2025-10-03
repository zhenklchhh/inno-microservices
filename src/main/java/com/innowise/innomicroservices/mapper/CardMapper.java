package com.innowise.innomicroservices.mapper;

import com.innowise.innomicroservices.dto.CardDto;
import com.innowise.innomicroservices.model.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Evgeniy Zaleshchenok
 */
@Mapper(componentModel = "spring")
public interface CardMapper {
    Card toEntity(CardDto createCardRequestDto);

    @Mapping(source = "user.id", target = "userId")
    CardDto toResponseDto(Card card);
}
