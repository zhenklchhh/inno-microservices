package com.innowise.userservice.mapper;

import com.innowise.userservice.model.UserDto;
import com.innowise.userservice.model.entity.User;
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
