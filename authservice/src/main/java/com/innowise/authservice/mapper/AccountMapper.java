package com.innowise.authservice.mapper;

import com.innowise.authservice.model.AccountDto;
import com.innowise.authservice.model.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Evgeniy Zaleshchenok
 */
@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    Account toEntity(AccountDto accountDto);

    @Mapping(target = "password", ignore = true)
    AccountDto toAccountDto(Account entity);
}
