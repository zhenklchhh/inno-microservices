package com.innowise.authservice.service.impl;

import com.innowise.authservice.model.entity.Account;
import com.innowise.authservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Account account = accountRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User with login: " + login + " not found"));

        return new org.springframework.security.core.userdetails.User(
                account.getLogin(),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + account.getUserRole().name()))
        );
    }
}
