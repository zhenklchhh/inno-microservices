package com.innowise.innomicroservices.mapper;

import com.innowise.innomicroservices.dto.CardDTO;
import com.innowise.innomicroservices.model.Card;
import org.mapstruct.Mapper;

/**
 * @author Evgeniy Zaleshchenok
 */
@Mapper(componentModel = "spring")
public interface CardMapper {
    Card toEntity(CardDTO createCardRequestDto);
    CardDTO toResponseDto(Card card);
}
