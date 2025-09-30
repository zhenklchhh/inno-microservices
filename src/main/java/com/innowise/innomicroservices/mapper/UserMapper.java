package com.innowise.innomicroservices.mapper;

import com.innowise.innomicroservices.dto.UserDTO;
import com.innowise.innomicroservices.model.User;
import org.mapstruct.Mapper;

/**
 * @author Evgeniy Zaleshchenok
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDTO dto);
    UserDTO toResponseDto(User entity);
}
