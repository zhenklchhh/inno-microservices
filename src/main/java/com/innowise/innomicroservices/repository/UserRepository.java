package com.innowise.innomicroservices.repository;

import com.innowise.innomicroservices.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query("UPDATE User u SET u.name = :name WHERE u.id = :id")
    void updateNameById(@Param("id") Long id, @Param("name") String name);
}
