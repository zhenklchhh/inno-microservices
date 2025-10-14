package com.innowise.authservice.service.impl;

import com.innowise.authservice.exception.AccountAlreadyExistsException;
import com.innowise.authservice.exception.AccountNotFoundException;
import com.innowise.authservice.mapper.AccountMapper;
import com.innowise.authservice.model.AccountDto;
import com.innowise.authservice.model.entity.Account;
import com.innowise.authservice.repository.AccountRepository;
import com.innowise.authservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author Evgeniy Zaleshchenok
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountMapper accountMapper;

    @Override
    public AccountDto findAccountByLogin(String login) {
        Account account = accountRepository.findByLogin(login)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with login: " + login));
        return accountMapper.toAccountDto(account);
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        if (accountRepository.existsByLogin(accountDto.login())){
            throw new AccountAlreadyExistsException("Account with login "  + accountDto.login() + "already exists");
        }
        Account account = accountMapper.toEntity(accountDto);
        account.setPassword(passwordEncoder.encode(accountDto.password()));
        accountRepository.save(account);
        return accountDto;
    }
}
