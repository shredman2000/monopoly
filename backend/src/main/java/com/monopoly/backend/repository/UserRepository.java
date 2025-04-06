package com.monopoly.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monopoly.backend.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
