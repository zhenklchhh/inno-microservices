package com.innowise.authservice.service;

import com.innowise.authservice.model.AccountDto;

/**
 * @author Evgeniy Zaleshchenok
 */
public interface AccountService {
    AccountDto findAccountByLogin(String login);
    AccountDto createAccount(AccountDto accountDto);
}
