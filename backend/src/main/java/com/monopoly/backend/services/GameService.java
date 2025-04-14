package com.monopoly.backend.services;

import java.util.*;

import org.springframework.stereotype.Service;

import com.monopoly.backend.models.Game;
import com.monopoly.backend.models.TileState;
import com.monopoly.backend.models.PlayerState;
import com.monopoly.backend.repository.GameRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;


@Service
public class GameService {

    private final BoardInitializer boardInitializer;
    private SimpMessagingTemplate messagingTemplate;
    private GameRepository gameRepository;

    public GameService(BoardInitializer boardInitializer, SimpMessagingTemplate messagingTemplate, GameRepository gameRepository) {
        this.boardInitializer = boardInitializer;
        this.messagingTemplate = messagingTemplate;
        this.gameRepository = gameRepository;
    }

    public Game createNewGame(String username) {
        Game game = new Game();
        game.addPlayer(username);

        List<TileState> tiles = boardInitializer.createTiles(game);
        game.setTileStates(tiles);

        return game;
    }

    /**
     * Call this after everything that should end the players turn!!!
     */
    public void finalizeTurn(Game game, String username) {
        System.out.println("finalizing turn");
        int currentTurn = game.getTurnIndex();
        int totalPlayers = game.getPlayerStates().size();
        game.setTurnIndex((currentTurn + 1) % totalPlayers);

        gameRepository.save(game);
        
        messagingTemplate.convertAndSend("/topic/gameUpdates/" + game.getGameId(), game);
    }

    /**
     * 
     * @params 
     */
    public void handlePlayerMove(Game game, int newPos) {
        int turnIndex = game.getTurnIndex();
        PlayerState ps = game.getPlayerStates().get(turnIndex);
        String username = game.getPlayerUsernames().get(turnIndex);

        //retrieve tile landed on
        TileState landedTile = game.getTileStates().stream()
            .filter(tile -> tile.getTileIndex() == newPos)
            .findFirst()
            .orElse(null);

        
        String tileType = landedTile.getType();
        System.out.println("in handle player move, tileType = " + tileType);
        Map<String, Object> response = new HashMap<>();
        switch(tileType) {
            case "go":
                finalizeTurn(game, username);
                break;
            case "property":
                // if unowned, give choice to buy
                if (!landedTile.isOwned()) { // can buy
                    response.put("action", "offer_purchase");
                    response.put("type", "property");
                    response.put("tileName", landedTile.getTileName());
                    response.put("price", landedTile.getPrice());
                    response.put("player", username);
                    break;
                }

                // if owned property
                if (landedTile.isOwned()) {
                    String owner = landedTile.getOwnerUsername();
                    if (owner.equals(username)) {
                        // you own this
                        response.put("action", "continue");

                        finalizeTurn(game, username);
                        break;
                    }
                    else {
                        int houses = landedTile.getHouseCount();
                        int rentToPay = 0;
                        switch(houses) {
                            case 0:
                                rentToPay = landedTile.getRent0House();
                                break;
                            case 1:
                                rentToPay = landedTile.getRent1House();
                                break;
                            case 2:
                                rentToPay = landedTile.getRent2House();
                                break;
                            case 3:
                                rentToPay = landedTile.getRent3House();
                                break;
                            case 4:
                                rentToPay = landedTile.getRent4House();
                                break;
                            case 5: //hotel
                                rentToPay = landedTile.getRentHotel();
                                break;
                        }
                        response.put("action", "pay_rent");
                        response.put("owner", owner);
                        response.put("rent", rentToPay);
                        response.put("player", username);
                    }
                }
                break;
            case "tax":
                // calculate owed and pay, add to free parking total
                finalizeTurn(game, username);
                break;
            case "community_chest":
                // draw card
                finalizeTurn(game, username);
                break;
            case "chance":
                // draw card
                finalizeTurn(game, username);
                break;
            case "railroad":
                finalizeTurn(game, username);
                break;
            case "utility":
                // if someone elses property, pay up.
                finalizeTurn(game, username);
                // if unowned, give choice to buy
                break;
            case "free_parking":
                // give player amount of money on free parking
                finalizeTurn(game, username);
                break;
            case "go_to_jail":
                // update players pos to jail tile + set some ticker for 3 turns.
                finalizeTurn(game, username);
                break;          
            case "jail":
                finalizeTurn(game, username);
                break;
        }
        
        
        System.out.println("Sending tile action: " + response);
        messagingTemplate.convertAndSend("/topic/tileAction/" + game.getGameId(), response);
    }

}