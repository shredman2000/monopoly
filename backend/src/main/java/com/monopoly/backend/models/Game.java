package com.monopoly.backend.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "games")
public class Game {
    @Id
    String gameId;
    

    private int turnIndex; // whose turn it is
    private boolean started;
    
    @ManyToMany
    List<User> players;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<PlayerState> playerStates = new ArrayList<>();


    int numPlayers;

    @ElementCollection(fetch= FetchType.EAGER)
    private List<String> playerUsernames = new ArrayList<>();



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

    public List<User> getPlayers() {
        return players;
    }

    public void setPlayers(List<User> players) {
        this.players = players;
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
}