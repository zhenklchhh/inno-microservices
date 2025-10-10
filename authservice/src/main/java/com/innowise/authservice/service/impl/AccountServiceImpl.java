package com.innowise.authservice.service.impl;

import com.innowise.authservice.exception.UserAlreadyExistsException;
import com.innowise.authservice.mapper.AccountMapper;
import com.innowise.authservice.model.AccountDto;
import com.innowise.authservice.model.entity.Account;
import com.innowise.authservice.repository.AccountRepository;
import com.innowise.authservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author Evgeniy Zaleshchenok
 */
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountMapper accountMapper;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountMapper = accountMapper;
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        if (accountRepository.existsByLogin(accountDto.login())){
            throw new UserAlreadyExistsException("User already exists with login: " + accountDto.login());
        }
        Account account = accountMapper.toEntity(accountDto);
        account.setPassword(passwordEncoder.encode(accountDto.password()));
        accountRepository.save(account);
        return accountDto;
    }

    @Override
    public AccountDto findAccountByLogin(String login) {
        return null;
    }
}
