package com.innowise.authservice.service;

import com.innowise.authservice.model.AccountDto;

/**
 * @author Evgeniy Zaleshchenok
 */
public interface AccountService {
    AccountDto createAccount(AccountDto accountDto);
    AccountDto findAccountByLogin(String login);
}
