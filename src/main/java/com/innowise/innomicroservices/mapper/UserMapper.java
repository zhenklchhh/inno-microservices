package com.innowise.innomicroservices.mapper;

import com.innowise.innomicroservices.model.UserDto;
import com.innowise.innomicroservices.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Evgeniy Zaleshchenok
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "cards", ignore = true)
    User toEntity(UserDto dto);
    UserDto toResponseDto(User entity);
}
