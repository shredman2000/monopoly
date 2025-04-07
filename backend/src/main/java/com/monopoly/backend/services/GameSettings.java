package com.monopoly.backend.services;

import java.util.List;

public class GameSettings {
    private List<String> playerUsernames;
    private int numPlayers;

    public GameSettings() {}

    public GameSettings(List<String> playerUsernames, int numPlayers) {
        this.playerUsernames = playerUsernames;
        this.numPlayers = numPlayers;
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
}
