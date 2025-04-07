package com.monopoly.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monopoly.backend.models.PlayerState; 



public interface PlayerStateRepository extends JpaRepository<PlayerState, Long> {}