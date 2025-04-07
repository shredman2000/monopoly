package com.monopoly.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monopoly.backend.models.Game;

public interface GameRepository extends JpaRepository<Game, String> {
}
