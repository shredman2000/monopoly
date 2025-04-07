package com.monopoly.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monopoly.backend.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByUsernameIn(List<String> usernames);
    Optional<User> findByUsername(String username);
}
