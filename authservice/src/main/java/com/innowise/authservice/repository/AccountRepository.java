package com.innowise.authservice.repository;

import com.innowise.authservice.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Evgeniy Zaleshchenok
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByLogin(String login);
    boolean existsByLogin(String login);
}
