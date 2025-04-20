package com.monopoly.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monopoly.backend.models.AuctionState;

public interface AuctionStateRepository extends JpaRepository<AuctionState, Long> {
    
}