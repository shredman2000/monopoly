package com.monopoly.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monopoly.backend.models.Game;
import com.monopoly.backend.models.TradeState;

public interface TradeStateRepository extends JpaRepository<TradeState, Long> {
    Optional<TradeState> findByGame_GameId(String gameId);
    Optional<TradeState> findByGame(Game game);
}
