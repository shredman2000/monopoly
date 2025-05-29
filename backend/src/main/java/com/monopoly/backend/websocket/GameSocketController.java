package com.monopoly.backend.websocket;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monopoly.backend.models.AuctionState;
import com.monopoly.backend.models.Game;
import com.monopoly.backend.models.PlayerState;
import com.monopoly.backend.models.TileState;
import com.monopoly.backend.models.TradeState;
import com.monopoly.backend.repository.AuctionStateRepository;
import com.monopoly.backend.repository.GameRepository;
import com.monopoly.backend.repository.TradeStateRepository;
import com.monopoly.backend.services.GameService;

@Controller
public class GameSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private AuctionStateRepository auctionStateRepository;

    @Autowired
    private TradeStateRepository tradeStateRepository;

    /**
     * websocket for joining a lobby
     * @return messageTemplate of game usernames and gameId
     */
    @MessageMapping("/joinLobby")
    public void handleJoinLobby(Map<String, String> joinMsg) {
        String gameId = joinMsg.get("gameId");
        String username = joinMsg.get("username");

        // Broadcast to all players in the game lobby
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game.getNumPlayers() == 6) {
            System.out.println("Game full");
            return;
        }
        if (!game.getPlayerUsernames().contains(username)) {
            game.addPlayer(username);
            gameRepository.save(game);
        }
        System.out.println("Broadcasting players: " + game.getPlayerUsernames());
        messagingTemplate.convertAndSend("/topic/lobby/" + gameId, game.getPlayerUsernames());
    }

    /**
     * websocket for returning the list of lobby players
     * @return players in lobby, and admin
     */
    @MessageMapping("/getLobbyPlayers")
    public void getLobbyPlayers(Map<String, String> msg) {
        String gameId = msg.get("gameId");
        System.out.println("Requesting lobby players");
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return;
        System.out.println("Broadcasting players from /getLobbyPlayers: " + game.getPlayerUsernames());

        messagingTemplate.convertAndSend("/topic/lobby/" + gameId, game.getPlayerUsernames());
    }

    /**
     * unsure whether this is being used or the rest endpoint
     */
    @MessageMapping("/startGame")
    public void handleStartGame(Map<String, String> msg) {
        String gameId = msg.get("gameId");
        Game game = gameRepository.findById(gameId).orElse(null);
        List<String> players = game.getPlayerUsernames();
        Collections.shuffle(players); 

        game.setPlayerUsernames(players);
        game.setTurnIndex(0);

        String firstPlayer = players.get(0);
        List<String> colors = List.of("red" , "blue" ," pink", "green", "yellow", "purple", "orange");
        for (PlayerState playerState : game.getPlayerStates()) { //set each players position to the first tile.
            playerState.setPosition(0);
            if (firstPlayer.equals(playerState.getUsername())) { playerState.setCanRoll(true); }
        }

        for (int i = 0; i < players.size(); i++ ) {
            String playerUsername = players.get(i);
            PlayerState ps = game.getPlayerStates().stream()
                .filter(player -> player.getUsername().equals(playerUsername)).findFirst().orElse(null);
            if (ps != null) {
                ps.setColor(colors.get(i % colors.size()));
            }
        }

        


        gameRepository.save(game);

        messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game); // send to scene.js
        messagingTemplate.convertAndSend("/topic/start/" + gameId, true); // send to waitingroom.js
    }


    /**
     * endpoint for rolling the dice.
     */
    @MessageMapping("/rollDice")
    public void rollDice(RollDiceRequest rollRequest) {
        System.out.println("/rollDice reached");
        String gameId = rollRequest.getGameId();
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) { 
            return; 
        }
        int turnIndex = game.getTurnIndex();
        List<PlayerState> players = game.getPlayerStates();

        PlayerState currPlayer = players.get(turnIndex);

        int dieOne = new Random().nextInt(6) + 1;
        int dieTwo = new Random().nextInt(6) + 1;
        int roll = dieOne + dieTwo;
        System.out.println("<<<<<<<<<<" + roll);
        int nextPos = (currPlayer.getPosition() + roll) % 40;
        // TODO: handle snake eyes here

        currPlayer.setCanRoll(false);
        // handlePlayerMove()
        currPlayer.setPosition(nextPos);

        gameRepository.save(game);

        messagingTemplate.convertAndSend("/topic/rolled/" + gameId, Map.of( "username", currPlayer.getUsername(),"roll", roll,"newPosition", nextPos));
    }

    /**
     * endpoint for starting the loop when a player lands on a tile
     */
    @MessageMapping("/handlePlayerLanding")
    public void handlePlayerLanding(Map<String, String> msg) {
        System.out.println("/handlePlayerLanding reached");
        String username = msg.get("username");
        String gameId = msg.get("gameId");
        int newPos = Integer.parseInt(msg.get("newPosition"));
        int roll = Integer.parseInt(msg.get("roll"));

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return;
        int turnIndex = game.getTurnIndex();
        String currentUsername = game.getPlayerUsernames().get(game.getTurnIndex());



        if (msg.get("type").equals("card")) {
            System.out.println("<<<<<<<<<<<<<<<<<<< REACHED THE SPOT IN handlePlayerLanding where we should be updating the players position");
            PlayerState ps = game.getPlayerStates().stream().filter(p -> p.getUsername().equals(currentUsername)).findFirst().orElse(null);
            System.out.println("<<<<<<<<<<<<<<<<<<<<< ps username = " + ps.getUsername() + 
                "|||and currentUsername = " + currentUsername + "||||||| and username = " + username);
            System.out.println("BEFORE SET: position = " + ps.getPosition());
            ps.setPosition(newPos);
            System.out.println("AFTER SET: position = " + ps.getPosition());
            System.out.println("AND newPos is supposed to be:" + newPos );
            System.out.println(">>> ps id: " + ps.getId() + " at hashCode: " + ps.hashCode());

            gameRepository.save(game);

        }

        gameService.handlePlayerMove(game, newPos, roll);
        //currPlayer.setPosition(newPos);

        gameRepository.save(game);

        gameRepository.flush();
        PlayerState confirm = game.getPlayerStates().stream()
            .filter(p -> p.getUsername().equals(username))
            .findFirst().orElse(null);
        System.out.println(">>> Confirmed saved position: " + confirm.getPosition());

        messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);
    }
    
    @MessageMapping("/buyProperty")
    public void buyProperty(Map<String, String> msg) {
        System.out.println("/buyProperty reached");
        String username = msg.get("username");
        String gameId = msg.get("gameId");
        String tileName = msg.get("tileName");

        Game game = gameRepository.findById(gameId).orElse(null);

        if (game == null) return;

       
         TileState tile = game.getTileStates().stream()
            .filter(t -> tileName.equals(t.getTileName()))
            .findFirst()
            .orElse(null);
        if (tile == null || tile.isOwned()) return;


        PlayerState player = game.getPlayerStates().stream()
            .filter(p -> username.equals(p.getUsername()))
            .findFirst()
            .orElse(null);
        if (player == null) return;

        int price = tile.getPrice();
        int playerBalance = player.getMoney();
        if (playerBalance < price) {
            gameService.postMoveState(game, username);
            //gameService.finalizeTurn(game, username);
            return;
        }
        player.setMoney(playerBalance - price);

        tile.setOwned(true);
        tile.setOwnerUsername(username);

        gameRepository.save(game);
        System.out.println("<<<<<< TILE BOUGHT");
        //messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);
        gameService.postMoveState(game, username);
        //gameService.finalizeTurn(game, username);

    }

    /**
     * end point for starting an auction
     */
    @MessageMapping("/auction")
    public void auctionProperty(Map<String, String> msg) {
        String gameId = msg.get("gameId");
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return;

        //this line is for if we dont want the original person in the auction 
        //List<PlayerState> players = game.getPlayerStates().stream().filter(player -> !player.getUsername().equals(msg.get("username"))).toList();
        // or use this to have everyone part of auction
        List<PlayerState> players = game.getPlayerStates();

        Optional<TileState> tileToSell = game.getTileStates().stream().filter(tile -> tile.getTileName().equals(msg.get("tileName"))).findFirst();
        if (tileToSell.isEmpty()) { System.out.println("Error"); }
        TileState tile = tileToSell.get();

        List<String> playerUsernames = players.stream().map(PlayerState::getUsername).collect(Collectors.toList());
   
        AuctionState auctionState = new AuctionState(tile.getTileName(), playerUsernames,  msg.get("username"));
        auctionState.setGame(game);
        game.setAuctionState(auctionState);
        gameRepository.save(game);
        
        Map<String, Object> auctionUpdate = new HashMap<>();
        auctionUpdate.put("tilename", tile.getTileName());
        auctionUpdate.put("currentbid", auctionState.getCurrentBid());
        auctionUpdate.put("currentbidder", auctionState.getCurrentBidder());


        messagingTemplate.convertAndSend("/topic/auctionUpdates/" + gameId, auctionUpdate);
    }

    @MessageMapping("/auction/bid")
    public void bidAuction(Map<String, String> msg) {
        Map<String, Object> auctionUpdate = new HashMap<>();
        String gameId = msg.get("gameId");
        String bidderUsername = msg.get("bidder");
        int bid = Integer.parseInt(msg.get("amount"));

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return;
        PlayerState player = game.getPlayerStates().stream().filter(p -> p.getUsername().equals(bidderUsername)).findFirst().get();
        AuctionState auctionState = game.getAuctionState();
        int currBid = auctionState.getCurrentBid() + bid;

        if (currBid >= player.getMoney()) {
            auctionUpdate.put("action", "lowbalance");
            auctionUpdate.put("tilename", auctionState.getTileName());
            messagingTemplate.convertAndSend("/topic/auctionUpdates/" + gameId, auctionUpdate);
            return;
        }

        auctionState.makeBid(bidderUsername, currBid);
        auctionState.advanceTurn();

        
        gameRepository.save(game);

        auctionUpdate.put("tilename",auctionState.getTileName());
        auctionUpdate.put("currentbid", auctionState.getCurrentBid());
        auctionUpdate.put("currentbidder", auctionState.getCurrentBidder());
        messagingTemplate.convertAndSend("/topic/auctionUpdates/" + gameId, auctionUpdate);
    }

    /**
     *  Endpoint for leaving the auction
     * */ 
    @MessageMapping("/auction/pass")
    public void passAuction(Map<String, String> msg) {
        String bidder = msg.get("bidder");
        String gameId = msg.get("gameId");
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) { return; }
        Game game = gameOpt.get();

        AuctionState auction = game.getAuctionState();

        auction.removeBidder(bidder);
        Map<String, Object> auctionUpdate = new HashMap<>();
        // person won the tile
        if (auction.getActiveBidders().size() <= 1) {
            String winner = auction.getActiveBidders().get(0);
            TileState tileSold = game.getTileStates().stream().filter(tile -> tile.getTileName().equals(auction.getTileName())).findFirst().get();
            
            tileSold.setOwned(true);
            tileSold.setOwnerUsername(winner);
            game.setAuctionState(null);
            
            PlayerState winnerPS = game.getPlayerStates().stream().filter(ps -> ps.getUsername().equals(winner)).findFirst().get();
            int winningBid = auction.getCurrentBid();
            winnerPS.setMoney(winnerPS.getMoney() - winningBid);

            gameRepository.save(game);
            

            gameService.postMoveState(game, auction.getAuctionStarter());
            //gameService.finalizeTurn(game, auction.getAuctionStarter());
            Map<String, Object> endMessage = new HashMap<>();
            endMessage.put("action", "auction_won");
            endMessage.put("tileName", tileSold.getTileName());
            endMessage.put("winner", winner);
            endMessage.put("price", auction.getCurrentBid());

            messagingTemplate.convertAndSend("/topic/auctionUpdates/" + gameId, endMessage);
            return;
        }

        auction.advanceTurn();

        
        auctionUpdate.put("tilename",auction.getTileName());
        auctionUpdate.put("currentbid", auction.getCurrentBid());
        auctionUpdate.put("currentbidder", auction.getCurrentBidder());

        messagingTemplate.convertAndSend("/topic/auctionUpdates/" + gameId, auctionUpdate);
    }

    /**
     * endpoint for paying a user
     * 
     */
    @MessageMapping("/payUser")
    public void payUser(Map<String, String> msg) {
        System.out.println("<<<<<<<<<<<REACHED PAYUSER");
        String gameId = msg.get("gameId");
        String fromUser = msg.get("fromUsername");
        String toUser = msg.get("toUsername");

        int rent = Integer.parseInt(msg.get("amount"));

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {return;}

        PlayerState payingUser = game.getPlayerStates().stream()
            .filter(p -> p.getUsername().equals(fromUser))
            .findFirst().orElse(null);

        PlayerState userToBePaid = game.getPlayerStates().stream().filter(p -> p.getUsername().equals(toUser)).findFirst().orElse(null);
        if (payingUser == null || userToBePaid == null) { return; }

        payingUser.setMoney(payingUser.getMoney() - rent);
        userToBePaid.setMoney(userToBePaid.getMoney() + rent);

        gameRepository.save(game);

        //messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);
        gameService.postMoveState(game, fromUser);
        //gameService.finalizeTurn(game, fromUser);
    }

    @MessageMapping("/getGameState")
    public void handleGetGameState(Map<String, String> msg) {
        System.out.println("reached /getGameState");
        String gameId = msg.get("gameId");
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game != null) {
            messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);
        }
    }

    @MessageMapping("/finalizeTurn")
    public void handleFinalizeTurn(Map<String, String> msg) {
        String gameId = msg.get("gameId");
        String username = msg.get("username");
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return;
        //gameService.postMoveState(game, username);
        gameService.finalizeTurn(game, username);
    }


    @MessageMapping("/buildHouse")
    public void buildHouse(Map<String, String> msg) {
        String gameId = msg.get("gameId");
        String username = msg.get("username");
        String tileName = msg.get("tileName");

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return;

        PlayerState ps = game.getPlayerStates().stream().filter(player -> player.getUsername().equals(username)).findFirst().orElse(null);
        TileState ts = game.getTileStates().stream().filter(tile -> tile.getTileName().equals(tileName)).findFirst().orElse(null);
         if (ps == null || ts == null) return;

        int houseCost = ts.getHouseCost();

        

        ps.setMoney(ps.getMoney() - houseCost);
        ts.setHouseCount(ts.getHouseCount() + 1);

        gameService.updateHousePlacement(game, username);
        
        gameRepository.save(game);
        messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);
    }

    /////////////////////// TRADING STUFF
    @MessageMapping("/startTrade")
    public void startTrade(Map<String, String> msg) {
        String gameId = msg.get("gameId");
        String player1 = msg.get("username");
        String player2 = msg.get("name");

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {return;}


        Optional<TradeState> existing = tradeStateRepository.findByGame(game);
        if (existing.isPresent()) {
            // maybe reset state or just return
            return;
        }
        tradeStateRepository.findByGame_GameId(gameId).ifPresent(tradeStateRepository::delete);
        TradeState tradeState = new TradeState(game, player1, player2, player1);

        game.setTradeState(tradeState);
        gameRepository.save(game);
        messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);
        tradeStateRepository.save(tradeState);
        messagingTemplate.convertAndSend("/topic/tradeUpdates/" + gameId, tradeState);
        
    }

    @MessageMapping("/addTile")
    public void addTileToTrade(Map<String, String> msg) {
        String gameId = msg.get("gameId");
        String tileOwner = msg.get("tileOwner");
        String tileName = msg.get("tileName");
        
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {return;}

        Optional<TradeState> optTradeState = tradeStateRepository.findByGame_GameId(gameId);
        if (optTradeState.isEmpty()) { return; }
        TradeState tradeState = optTradeState.get();

        System.out.println(">>>>>>>>>> Sending trade update to /topic/tradeUpdates/" + gameId);
        try {
            System.out.println(new ObjectMapper().writeValueAsString(tradeState));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        TileState tile = game.getTileStates().stream().filter(t -> t.getTileName().equals(tileName)).findFirst().orElse(null);

        boolean alreadyOffered = tradeState.getTilesOffered1().stream()
            .anyMatch(t -> t.getTileName().equals(tileName)) ||
            tradeState.getTilesOffered2().stream()
            .anyMatch(t -> t.getTileName().equals(tileName));

        if (alreadyOffered){ return; }

        if (tileOwner.equals(tradeState.getPlayer1())) {
            tradeState.addTileOffered1(tile);
        } else if (tileOwner.equals(tradeState.getPlayer2())) {
            tradeState.addTileOffered2(tile);
        }


        //debugging
        tradeState.incrementCount();


        tradeState = tradeStateRepository.save(tradeState);
        game.setTradeState(tradeState);
        gameRepository.save(game);
        try {
            String json = new ObjectMapper().writeValueAsString(tradeState);
            messagingTemplate.convertAndSend("/topic/tradeUpdates/" + gameId, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //messagingTemplate.convertAndSend("/topic/tradeUpdates/" + gameId, tradeState);
        messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);
    }

    @MessageMapping("/removeTile")
    public void removeTileFromTrade(Map<String, String> msg) {
        String gameId = msg.get("gameId");
        String tileOwner = msg.get("tileOwner");
        String tileName = msg.get("tileName");

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {return;}

        TradeState tradeState = game.getTradeState();
        if (tradeState == null) { return; }

        TileState tile = game.getTileStates().stream().filter(t -> t.getTileName().equals(tileName)).findFirst().orElse(null);

         if (tileOwner.equals(tradeState.getPlayer1())) {
            tradeState.removeTileOffered1(tile);
        } else if (tileOwner.equals(tradeState.getPlayer2())) {
            tradeState.removeTileOffered2(tile);
        }

        tradeState = tradeStateRepository.save(tradeState);
        game.setTradeState(tradeState);
        gameRepository.save(game);
        try {
            String json = new ObjectMapper().writeValueAsString(tradeState);
            messagingTemplate.convertAndSend("/topic/tradeUpdates/" + gameId, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);
    }

    @MessageMapping("/setMoney")
    public void addMoneyToTrade(Map<String, String> msg) {
        String gameId = msg.get("gameId");
        String moneyFrom = msg.get("name");
        int money = Integer.parseInt(msg.get("val"));

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {return;}

        TradeState tradeState = game.getTradeState();
        if (tradeState == null) { return; }

        if (moneyFrom.equals(tradeState.getPlayer1())) {
            tradeState.setMoneyOffered1(money);
        } else if (moneyFrom.equals(tradeState.getPlayer2())) {
            tradeState.setMoneyOffered2(money);
        }

        tradeStateRepository.save(tradeState);
        game.setTradeState(tradeState);
        gameRepository.save(game);
        try {
            String json = new ObjectMapper().writeValueAsString(tradeState);
            messagingTemplate.convertAndSend("/topic/tradeUpdates/" + gameId, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);
    }

    @MessageMapping("/sendTrade")
    public void sendTrade(Map<String, String> msg) {
        String gameId = msg.get("gameId");

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {return;}

        TradeState tradeState = game.getTradeState();
        if (tradeState == null) { return; }

        String offerer = tradeState.getCurrentOfferingPlayer();


        tradeState.setTradeSent(true);

        tradeStateRepository.save(tradeState);
        messagingTemplate.convertAndSend("/topic/tradeUpdates/" + gameId, tradeState);

    }

    @MessageMapping("/editTrade")
    public void editTrade(Map<String, String> msg) {
        String gameId = msg.get("gameId");
        String editor = msg.get("username"); // the player making the edit

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return;

        TradeState tradeState = game.getTradeState();
        if (tradeState == null) return;

        tradeState.setTradeSent(false);
        String current = tradeState.getCurrentOfferingPlayer();
        String newOfferer = tradeState.getCurrentOfferingPlayer().equals(tradeState.getPlayer1())
            ? tradeState.getPlayer2()
            : tradeState.getPlayer1();

        tradeState.setCurrentOfferingPlayer(newOfferer);
        tradeState.setPlayer1Confirmed(false);
        tradeState.setPlayer2Confirmed(false);

        tradeStateRepository.save(tradeState);
        messagingTemplate.convertAndSend("/topic/tradeUpdates/" + gameId, tradeState);
        game.setTradeState(tradeState);
        gameRepository.save(game);
        messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);

    }

    

    

    @MessageMapping("/acceptTrade")
    public void acceptTrade(Map<String, String> msg) {
        String gameId = msg.get("gameId");
        String acceptingPlayer = msg.get("acceptingPlayer");

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {return;}

        TradeState tradeState = game.getTradeState();
        if (tradeState == null) { return; }

        if (tradeState.getPlayer1().equals(acceptingPlayer)) {
            tradeState.setPlayer1Confirmed(true);
        }
        if (tradeState.getPlayer2().equals(acceptingPlayer)) {
            tradeState.setPlayer2Confirmed(true);
        }


        if (tradeState.getTradeConfirmed()) {

            PlayerState p1 = game.getPlayerStates().stream().filter(p -> p.getUsername().equals(tradeState.getPlayer1())).findFirst().orElse(null);
            PlayerState p2 = game.getPlayerStates().stream().filter(p -> p.getUsername().equals(tradeState.getPlayer2())).findFirst().orElse(null);

            //transfer money
            p1.setMoney(p1.getMoney() + tradeState.getMoneyOffered2() - tradeState.getMoneyOffered1());
            p2.setMoney(p2.getMoney() + tradeState.getMoneyOffered1() - tradeState.getMoneyOffered2());

            // transfer tiles

            List<TileState> player1GivingTiles = tradeState.getTilesOffered1();
            List<TileState> player2GivingTiles = tradeState.getTilesOffered2();

            List<TileState> gameTiles = game.getTileStates();

            // send player1s tiles to player2
            for (TileState tile : player1GivingTiles) {
                for (TileState gameTile: gameTiles) {
                    if (gameTile.getTileName().equals(tile.getTileName())) {
                        gameTile.setOwnerUsername(tradeState.getPlayer2());
                        break;
                    }
                }
            }

            // send player2s tiles to player1
            for (TileState tile : player2GivingTiles) {
                for (TileState gameTile: gameTiles) {
                    if (gameTile.getTileName().equals(tile.getTileName())) {
                        gameTile.setOwnerUsername(tradeState.getPlayer1());
                        break;
                    }
                }
            }

            // wipe trade?

            tradeStateRepository.save(tradeState);
            messagingTemplate.convertAndSend("/topic/tradeUpdates/" + gameId, tradeState);
            game.setTradeState(null);
        }
        else {
            tradeStateRepository.save(tradeState);
            messagingTemplate.convertAndSend("/topic/tradeUpdates/" + gameId, tradeState);
            game.setTradeState(tradeState);
        }
        gameRepository.save(game);
        messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);

    }

    @MessageMapping("/rejectTrade")
    public void rejectTrade(Map<String, String> msg) {
        String gameId = msg.get("gameId");

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {return;}

        TradeState tradeState = game.getTradeState();
        if (tradeState == null) { return; }

        //tradeStateRepository.save(tradeState);
        //messagingTemplate.convertAndSend("/topic/tradeUpdates/" + gameId, tradeState);
        game.setTradeState(null);
        gameRepository.save(game);
        messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);
    }

    //TODO: @MessageMapping("/cancelTrade")


    @MessageMapping("/mortgageTile") 
    public void mortgageTile(Map<String, String> msg) {
        String gameId = msg.get("gameId");
        String username = msg.get("username");
        String tilename = msg.get("tilename");

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {return;}

        TileState tileToMortgage = game.getTileStates().stream().filter(t -> tilename.equals(t.getTileName())).findAny().orElse(null);
        if (tileToMortgage == null) { return; }

        tileToMortgage.setMortgaged(true);
        int mortgageValue = tileToMortgage.getCostToMortgage();

        PlayerState player = game.getPlayerStates().stream().filter(p -> username.equals(p.getUsername())).findAny().orElse(null);
        if (player == null) { return; }

        player.setMoney(player.getMoney() + mortgageValue);

        gameRepository.save(game);
        messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);    
    }

    @MessageMapping("/buyBackMortagedTile")
    public void buyBackMortgaged(Map<String, String> msg) {
        String gameId = msg.get("gameId");
        String username = msg.get("username");
        String tilename = msg.get("tilename");

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) { return; }

        TileState tileToBuy = game.getTileStates().stream().filter(t -> tilename.equals(t.getTileName())).findAny().orElse(null);
        if (tileToBuy == null) { return; }

        PlayerState player = game.getPlayerStates().stream().filter(p -> username.equals(p.getUsername())).findAny().orElse(null);
        if (player == null) { return; }

        // too broke
        if (tileToBuy.getCostToMortgage() > player.getMoney()) { return; }

        tileToBuy.setMortgaged(false);

        player.setMoney(player.getMoney() - tileToBuy.getCostToMortgage());
        
        gameRepository.save(game);
        messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);    
    }


    @MessageMapping("/confirmCommunityChest")
    public void confirmCommunityChest(Map<String, String> msg) {
        String gameId = msg.get("gameId");
        String username = msg.get("username");
        String action = msg.get("action");
        System.out.println("Received confirmCommunityChest from: " + username);

        

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) { return; }
        PlayerState player = game.getPlayerStates().stream().filter(p -> username.equals(p.getUsername())).findAny().orElse(null);
        if (player == null) { return; }
        String currentTurnPlayer = game.getPlayerUsernames().get(game.getTurnIndex());
        if (!username.equals(currentTurnPlayer)) { return; }
        
        ////////////
        if (action.equals("recievemoney")) {
            int money = Integer.parseInt(msg.get("money"));
            player.setMoney(player.getMoney() + money);
        }
        else if (action.equals("paymoney")) {
            System.out.println("Paying money community chest " + username);
            int money = Integer.parseInt(msg.get("money"));
            if (player.getMoney() - money <= 0) {
                player.setMoney(0);
            }
            else {
                player.setMoney(player.getMoney() - money);
            }
            TileState freeParking = game.getTileStates().stream().filter(t -> t.getTileName().equals("Free Parking")).findAny().orElse(null);
            freeParking.setFreeParkingTotal(freeParking.getFreeParkingTotal() + money);
        }
        else if (action.equals("advancetogo")) {
            player.setPosition(0);
            player.setMoney(player.getMoney() + 200);
        }
        else if (action.equals("gotojail")) {
            //TODO: implement after adding jail functionality
        }
        else if (action.equals("collectfromplayers")) {
            List<PlayerState> otherPlayers = game.getPlayerStates().stream().filter(p -> !p.getUsername().equals(username)).collect(Collectors.toList());
            for (PlayerState p : otherPlayers) {
                if (p.getMoney() >= 20) {
                    p.setMoney(p.getMoney() - 20);
                    player.setMoney(player.getMoney() + 20);
                }
                else {
                    player.setMoney(player.getMoney() + p.getMoney());
                    p.setMoney(0);
                }
            }
        }
        else if (action.equals("advancerandomtile")) {
            int currPlayerPos = player.getPosition();
            int randomPos = (int)(Math.random() * 40);
            Map<String, String> dummyMsg = new HashMap<>();
            

            dummyMsg.put("username", username);
            dummyMsg.put("gameId", gameId);
            dummyMsg.put("newPosition", Integer.toString(randomPos));
            dummyMsg.put("roll", Integer.toString(Math.abs(randomPos - currPlayerPos)));
            dummyMsg.put("type", "card");

            handlePlayerLanding(dummyMsg);
        }
        else if (action.equals("getoutofjailfree")) {

        }

        System.out.println(">>>>> Backend: calling postMoveState for " + username);

        gameService.postMoveState(game, username);
        gameRepository.save(game);
        messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);
        
    }


    ///////////////////////DTOS
    public static class PlayerMoveMessage {
        private String gameId;
        private String username;
        private int steps;

        public PlayerMoveMessage() {}

        public String getGameId() { return gameId; }
        public void setGameId(String gameId) { this.gameId = gameId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public int getSteps() { return steps; }
        public void setSteps(int steps) { this.steps = steps; }
    } 


    public static class RollDiceRequest {
        private String gameId;
        private String userName;


        public RollDiceRequest() {}

        public String getGameId() {
            return gameId;
        }
        public void setGameId(String gameId) {
            this.gameId = gameId;
        }

        public String getUsername() {
            return userName;
        }
        public void setUsername(String userName) {
            this.userName = userName;
        }



    }   
    public static class LobbyState {
        public List<String> players;
        public String admin;

        public LobbyState(List<String> players, String admin) {
            this.players = players;
            this.admin = admin;
        }
        public List<String> getPlayers() {
            return players;
        }
        public String getAdmin() {
            return admin;
        }
    }

}
