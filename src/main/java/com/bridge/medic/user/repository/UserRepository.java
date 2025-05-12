package com.bridge.medic.user.repository;

import com.bridge.medic.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByLogin(String login);

    @Query("SELECT u FROM User u WHERE u.email = :emailOrLogin OR u.login = :emailOrLogin")
    Optional<User> findByEmailOrLogin(@Param("emailOrLogin") String emailOrLogin);
}
