
package com.monopoly.backend.services;

import org.springframework.stereotype.Service;

@Service
public class JoinGameRequest {
    private String username;
    private String gameId;

    public JoinGameRequest() {}

    public JoinGameRequest(String username, String gameId) {
        this.username = username;
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}