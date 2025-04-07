package com.monopoly.backend.services;

import java.util.List;

import com.monopoly.backend.models.PlayerState;

public class GameStateResponse {
    private String gameId;
    private boolean started;
    private int turnIndex;
    private List<String> playerUsernames;
    private List<PlayerState> playerStates;



    public GameStateResponse(
        String gameId, boolean started, int turnIndex, List<String> playerUsernames, List<PlayerState> playerStates) {
        this.gameId = gameId;
        this.started = started;
        this.turnIndex = turnIndex;
        this.playerUsernames = playerUsernames;
        this.playerStates = playerStates;
    }


        public String getGameId() { return gameId; }
    public boolean isStarted() { return started; }
    public int getTurnIndex() { return turnIndex; }
    public List<String> getPlayerUsernames() { return playerUsernames; }
    public List<PlayerState> getPlayerStates() { return playerStates; }

}