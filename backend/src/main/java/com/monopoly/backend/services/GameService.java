package com.monopoly.backend.services;

import java.util.*;

import org.springframework.stereotype.Service;

import com.monopoly.backend.models.Game;
import com.monopoly.backend.models.TileState;
import com.monopoly.backend.models.PlayerState;

import org.springframework.messaging.simp.SimpMessagingTemplate;


@Service
public class GameService {

    private final BoardInitializer boardInitializer;
    private SimpMessagingTemplate messagingTemplate;

    public GameService(BoardInitializer boardInitializer, SimpMessagingTemplate messagingTemplate) {
        this.boardInitializer = boardInitializer;
        this.messagingTemplate = messagingTemplate;
    }

    public Game createNewGame(String username) {
        Game game = new Game();
        game.addPlayer(username);

        List<TileState> tiles = boardInitializer.createTiles(game);
        game.setTileStates(tiles);

        return game;
    }

    /**
     * 
     * @params 
     */
    public void handlePlayerMove(Game game, String username, int newPos) {
        System.out.println("reached handlePlayerMove in gameservice");
        for (PlayerState ps : game.getPlayerStates()) {
            if (ps.getUsername() == username) {
                System.out.println(ps.getUsername() + " vs " + username);
                ps.setPosition(newPos);
                break;
            } 
        }
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
                break;
            case "community_chest":
                // draw card
                break;
            case "chance":
                // draw card
                break;
            case "railroad":

                break;
            case "utility":
                // if someone elses property, pay up.

                // if unowned, give choice to buy
                break;
            case "free_parking":
                // give player amount of money on free parking
                break;
            case "go_to_jail":
                // update players pos to jail tile + set some ticker for 3 turns.
                break;            
        }
        
        
        System.out.println("Sending tile action: " + response);
        messagingTemplate.convertAndSend("/topic/tileAction/" + game.getGameId(), response);
    }

}