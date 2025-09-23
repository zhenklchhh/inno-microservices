package com.innowise.innomicroservices.mapper;

import com.innowise.innomicroservices.dto.CreateUserRequestDto;
import com.innowise.innomicroservices.dto.UserResponseDto;
import com.innowise.innomicroservices.model.User;
import org.mapstruct.Mapper;

/**
 * @author Evgeniy Zaleshchenok
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(CreateUserRequestDto dto);
    UserResponseDto toResponseDto(User entity);
}
