package com.innowise.userservice.mapper;

import com.innowise.userservice.model.CardDto;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.model.entity.Card;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Evgeniy Zaleshchenok
 */
@Mapper(componentModel = "spring")
public abstract class CardMapper {

    @Autowired
    protected UserRepository userRepository;

    @Mapping(target = "user", source = "userId")
    public abstract Card toEntity(CardDto createCardRequestDto);

    @Mapping(source = "user.id", target = "userId")
    public abstract CardDto toResponseDto(Card card);

    protected User userFromId(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }
}