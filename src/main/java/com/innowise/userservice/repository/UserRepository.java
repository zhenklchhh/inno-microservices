package com.innowise.userservice.repository;

import com.innowise.userservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author Evgeniy Zaleshchenok
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    List<User> findUsersByIds(@Param("ids") List<Long> ids);

}
