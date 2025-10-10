package com.innowise.authservice.mapper;

import com.innowise.authservice.model.AccountDto;
import com.innowise.authservice.model.entity.Account;
import org.mapstruct.Mapper;

/**
 * @author Evgeniy Zaleshchenok
 */
@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDto toAccountDto(Account account);
    Account toEntity(AccountDto accountDto);
}
