package com.innowise.authservice.service;

import com.innowise.authservice.model.AccountDto;
import com.innowise.authservice.model.RegistrationRequestDto;

/**
 * Service interface for managing user accounts.
 * This service handles the lifecycle of an account, such as creation and retrieval.
 *
 * @author Evgeniy Zaleshchenok
 */
public interface AccountService {

    /**
     * Finds an account by its unique login identifier.
     * This method is primarily used for internal checks and retrieving account details.
     *
     * @param login The unique login (typically an email) of the account to find.
     * @return An {@link AccountDto} representing the found account.
     * @throws com.innowise.authservice.exception.AccountNotFoundException if no account with the given login exists.
     */
    AccountDto findAccountByLogin(String login);

    /**
     * Creates a new user account with the provided details.
     * This method is responsible for validating the input, hashing the password,
     * and persisting the new account to the database.
     *
     * @param accountDto A {@link AccountDto} containing the necessary information for the new account,
     *                   including the raw password.
     * @return An {@link AccountDto} representing the newly created account (typically without the password).
     * @throws com.innowise.authservice.exception.AccountAlreadyExistsException if an account with the same login already exists.
     */
    AccountDto createAccount(AccountDto accountDto);
}