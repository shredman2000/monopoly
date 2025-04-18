package com.monopoly.backend.services;

import java.util.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.monopoly.backend.models.Game;
import com.monopoly.backend.models.PlayerState;
import com.monopoly.backend.models.TileState;
import com.monopoly.backend.repository.GameRepository;


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
    public void handlePlayerMove(Game game, int newPos, int roll) {
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
                        response.put("type", "property");
                        response.put("owner", owner);
                        response.put("rent", rentToPay);
                        response.put("player", username);

                    }
                }
                break;
            case "tax":
                // calculate owed and pay, add to free parking total
                if (landedTile.getTileName().equals("Income Tax")) {// = lowest of 10% of players balance or 200 dollars
                    int incomeTax = 200;
                    if ((ps.getMoney() * .1) > 200 ) { incomeTax = 200; }
                    else { incomeTax = (int)(ps.getMoney() * .1); }  
                    ps.setMoney(ps.getMoney() - incomeTax);
                    TileState freeParking = game.getTileStates().stream().filter(tile -> tile.getTileName().equals("Free Parking")).findFirst().orElse(null);
                    if (freeParking == null) { return; }
                    freeParking.setFreeParkingTotal(freeParking.getFreeParkingTotal() + incomeTax);
                    response.put("action", "pay_tax");
                    response.put("amount", incomeTax);
                    response.put("player", username);
                    response.put("type", "tax");
                    response.put("taxType", "income");
                    response.put("freeparkingtotal", freeParking.getFreeParkingTotal());
                    finalizeTurn(game, username);
                    break;
                }
                else if(landedTile.getTileName().equals("Luxury Tax")) { // pay 75
                    int luxuryTax = 75;
                    ps.setMoney(ps.getMoney() - luxuryTax);
                    TileState freeParking = game.getTileStates().stream().filter(tile -> tile.getTileName().equals("Free Parking")).findFirst().orElse(null);
                    if (freeParking == null) { return; }
                    freeParking.setFreeParkingTotal(freeParking.getFreeParkingTotal() + luxuryTax);
                    response.put("action", "pay_tax");
                    response.put("amount", luxuryTax);
                    response.put("player", username);
                    response.put("type", "tax");
                    response.put("taxType", "luxury");
                    response.put("freeparkingtotal", freeParking.getFreeParkingTotal());
                    finalizeTurn(game, username);
                    break;
                }
                break;
            case "community_chest":
                // draw card
                finalizeTurn(game, username);
                break;
            case "chance":
                // draw card
                finalizeTurn(game, username);
                break;
            case "railroad": // check how many other railroads the player owns
                if (!landedTile.isOwned()) { // buy property
                    System.out.println("<<<<<<<<<<<<<<<reached buy railroad");  
                    response.put("action", "offer_purchase");
                    response.put("type", "railroad");
                    response.put("tileName", landedTile.getTileName());
                    response.put("price", landedTile.getPrice());
                    response.put("player", username);
                    break;
                }
                else {
                    System.out.println("<<<<<<<<<<<<<<<<<<<<<<reached railroad owned by other user");
                    int numRROwned = 1;
                    List<TileState> tiles = game.getTileStates();
                    for (TileState tile : tiles) {
                        if (tile.getType().equals("railroad") && username.equals(tile.getOwnerUsername())) { 
                            numRROwned++; 
                        }
                    } 
                    
                    int rent = 0;  
                    switch(numRROwned) {
                        case 1:
                            rent = landedTile.getRent0House();
                            break;
                        case 2:
                            rent = landedTile.getRent1House();
                            break;
                        case 3: 
                            rent = landedTile.getRent2House();
                            break;
                        case 4:
                            rent = landedTile.getRent3House();
                            break;
                    }
                    response.put("action", "pay_rent");
                    response.put("owner", landedTile.getOwnerUsername());
                    response.put("rent", rent);
                    response.put("player", ps);
                }
                break;
            case "utility": 
                
                if (!landedTile.isOwned()) { // if unowned, give choice to buy
                    response.put("action", "offer_purchase");
                    response.put("type", "utility");
                    response.put("tileName", landedTile.getTileName());
                    response.put("price", landedTile.getPrice());
                    response.put("player", username);
                    break;
                }
                else {// if someone elses property, pay up
                // if owner has 1 utility: 4x the dice roll, if they own 2 utilites: 10x the dice roll
                    String ownerUsername = landedTile.getOwnerUsername();
                    if (!ownerUsername.equals(username)) {
                        List<TileState> ownedUtils = game.getTileStates().stream().filter(tile -> tile.getType().equals("utility") && tile.getOwnerUsername().equals(ownerUsername)).toList();
                        int rent = 0;
                        if (ownedUtils.size() == 1) {
                            rent = 4 * roll;
                        }
                        else if (ownedUtils.size() == 2) {
                            rent = 10 * roll;
                        }
                        response.put("action", "pay_rent");
                        response.put("type", "utility");
                        response.put("tileName", landedTile.getTileName());
                        response.put("player", username);
                        response.put("owner", ownerUsername);
                        response.put("rent", rent);
                        break;
                    }
                    else {// you own
                        finalizeTurn(game, username);
                        break;
                    }
                }
            case "free_parking":
                // give player amount of money on free parking
                TileState freeParkingTile = game.getTileStates().stream().filter(tile -> tile.getTileName().equals("Free Parking")).findFirst().orElse(null);
                int amountOnFreeParking = freeParkingTile.getFreeParkingTotal();
                freeParkingTile.setFreeParkingTotal(0); 
                ps.setMoney(ps.getMoney() + amountOnFreeParking);

                response.put("action", "free_parking");
                response.put("player", username);
                response.put("amount", amountOnFreeParking);
                response.put("freeparkingtotal",freeParkingTile.getFreeParkingTotal());
                response.put("type", "free parking");
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