package com.monopoly.backend.models;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "games")
public class Game {
    @Id
    String gameId;
    
    @ManyToMany
    List<User> players;

    int numPlayers;

    @ElementCollection
    private List<String> playerUsernames = new ArrayList<>();

    // default constructor dont delete
    public Game() {}

    //CONSTRUCTOR FOR NEW GAME CREATION
    public Game(List<User> players) {
        this.gameId = UUID.randomUUID().toString();
        this.players = players;
        this.numPlayers = players.size();
        this.playerUsernames = players.stream()
            .map(User::getUsername)
            .collect(Collectors.toList());
    }

    public String getGameId() {
        return this.gameId;
    }

    public List<User> getPlayers() {
        return players;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public List<String> getPlayerUsernames() {
        return this.playerUsernames;
    }

    public void setNumPlayers(int count) {
       this.numPlayers = count;
    }
}