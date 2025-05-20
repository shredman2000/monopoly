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

import com.monopoly.backend.models.AuctionState;
import com.monopoly.backend.models.Game;
import com.monopoly.backend.models.PlayerState;
import com.monopoly.backend.models.TileState;
import com.monopoly.backend.repository.AuctionStateRepository;
import com.monopoly.backend.repository.GameRepository;
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
        gameService.handlePlayerMove(game, newPos, roll);
        //currPlayer.setPosition(newPos);

        gameRepository.save(game);

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
