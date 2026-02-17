package com.monopoly.backend.services;

import java.util.List;

public class GameSettings {
    private List<String> playerUsernames;
    private int numPlayers;
    private Boolean devMode;

    public GameSettings() {}

    public GameSettings(List<String> playerUsernames, int numPlayers, Boolean devMode) {
        this.playerUsernames = playerUsernames;
        this.numPlayers = numPlayers;
        this.devMode = devMode;
    }
    
    public List<String> getPlayerUsernames() {
        return playerUsernames;
    }

    public void setPlayerUsernames(List<String> playerUsernames) {
        this.playerUsernames = playerUsernames;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public Boolean getDevMode() {
        return devMode;
    }
    public void setDevMode(Boolean devMode) {
        this.devMode = devMode;
    }
}
