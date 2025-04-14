package com.monopoly.backend.websocket;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.monopoly.backend.models.Game;
import com.monopoly.backend.models.PlayerState;
import com.monopoly.backend.models.TileState;
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
     * @return players in lobby
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
        
        for (PlayerState playerState : game.getPlayerStates()) { //set each players position to the first tile.
            playerState.setPosition(0); 
        }

        gameRepository.save(game);

        messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game); // send to scene.js
        messagingTemplate.convertAndSend("/topic/start/" + gameId, true); // send to waitingroom.js
    }



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

        // handlePlayerMove()
        currPlayer.setPosition(nextPos);

        //game.setTurnIndex((turnIndex + 1) % game.getPlayerStates().size()); // increment turn want to handle after the player does their stuff. 
        gameRepository.save(game);

        messagingTemplate.convertAndSend("/topic/rolled/" + gameId, Map.of( "username", currPlayer.getUsername(),"roll", roll,"newPosition", nextPos));
    }


    @MessageMapping("/handlePlayerLanding")
    public void handlePlayerLanding(Map<String, String> msg) {
        System.out.println("/handlePlayerLanding reached");
        String username = msg.get("username");
        String gameId = msg.get("gameId");
        int newPos = Integer.parseInt(msg.get("newPosition"));

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return;
        int turnIndex = game.getTurnIndex();
        PlayerState currPlayer = game.getPlayerStates().get(turnIndex);
        gameService.handlePlayerMove(game, username, newPos);
        //currPlayer.setPosition(newPos);
        game.setTurnIndex((game.getTurnIndex() + 1) % game.getPlayerStates().size()); // set turn index

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
        if (playerBalance < price) {return;}
        player.setMoney(playerBalance - price);

        tile.setOwned(true);
        tile.setOwnerUsername(username);

        gameRepository.save(game);
        System.out.println("<<<<<< TILE BOUGHT");
        messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);
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
}
