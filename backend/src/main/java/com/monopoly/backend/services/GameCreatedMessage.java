package com.monopoly.backend.services;

public class GameCreatedMessage {
    public String gameId;
    public String message;

    public GameCreatedMessage(String gameId, String message) {
        this.gameId = gameId;
        this.message = message;
    }
}