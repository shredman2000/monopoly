package com.monopoly.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.monopoly.backend.models.Game;
import com.monopoly.backend.models.TileState;


@Service
public class GameService {

    private final BoardInitializer boardInitializer;

    public GameService(BoardInitializer boardInitializer) {
        this.boardInitializer = boardInitializer;
    }

    public Game createNewGame(String username) {
        Game game = new Game();
        game.addPlayer(username);

        List<TileState> tiles = boardInitializer.createTiles(game);
        game.setTileStates(tiles);

        return game;
    }

}