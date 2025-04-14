package com.monopoly.backend.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "games")
public class Game {
    @Id
    String gameId;
    

    private int turnIndex; // whose turn it is
    private boolean started;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<PlayerState> playerStates = new ArrayList<>();


    int numPlayers;

    @ElementCollection(fetch= FetchType.EAGER)
    private List<String> playerUsernames = new ArrayList<>();

    // map to list of tile states
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private List<TileState> tileStates = new ArrayList<>();


    // default constructor dont delete
    public Game() {
        this.gameId = UUID.randomUUID().toString();
        
    }

    public void addPlayer(String username) {
        playerUsernames.add(username);
        playerStates.add(new PlayerState(username, this));
        numPlayers++;
    }

    public String getGameId() {
        return gameId;
    }

    public int getTurnIndex() {
        return turnIndex;
    }

    public void setTurnIndex(int turnIndex) {
        this.turnIndex = turnIndex;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public List<PlayerState> getPlayerStates() {
        return playerStates;
    }

    public void setPlayerStates(List<PlayerState> playerStates) {
        this.playerStates = playerStates;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public List<String> getPlayerUsernames() {
        return playerUsernames;
    }

    public void setPlayerUsernames(List<String> playerUsernames) {
        this.playerUsernames = playerUsernames;
    }
    public List<TileState> getTileStates() {
        return tileStates;
    }

    public void setTileStates(List<TileState> tileStates) {
        this.tileStates = tileStates;
    }


    private void createGameTiles() {

    }
}