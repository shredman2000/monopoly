package com.monopoly.backend.websocket;

import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.monopoly.backend.models.Game;
import com.monopoly.backend.models.PlayerState;
import com.monopoly.backend.repository.GameRepository;

@Controller
public class GameSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private GameRepository gameRepository;

    // receives from /app/gameState
    @MessageMapping("/playerMove")
    @SendTo("/topic/gameUpdates")
    public Game handlePlayerMove(PlayerMoveMessage move) {
        Game game = gameRepository.findById(move.getGameId()).orElse(null);
        if (game == null) return null;

        for (PlayerState playerState : game.getPlayerStates()) {
            if (playerState.getUsername().equals(move.getUsername())) {
                playerState.setPosition(playerState.getPosition() + move.getSteps());
            }
        }
        gameRepository.save(game);
        return game;
    }

    
    @MessageMapping("/joinLobby")
    public void handleJoinLobby(Map<String, String> joinMsg) {
        String gameId = joinMsg.get("gameId");
        String username = joinMsg.get("username");

        // Broadcast to all players in the game lobby
        Game game = gameRepository.findById(gameId).orElse(null);

        if (!game.getPlayerUsernames().contains(username)) {
            game.addPlayer(username);
            gameRepository.save(game);
        }

        
        System.out.println("Broadcasting players: " + game.getPlayerUsernames());
        messagingTemplate.convertAndSend("/topic/lobby/" + gameId, game.getPlayerUsernames());
    }

    @MessageMapping("/getLobbyPlayers")
    public void getLobbyPlayers(Map<String, String> msg) {
        String gameId = msg.get("gameId");
        System.out.println("Requesting lobby players");
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return;
        

        System.out.println("Broadcasting players from /getLobbyPlayers: " + game.getPlayerUsernames());

        messagingTemplate.convertAndSend("/topic/lobby/" + gameId, game.getPlayerUsernames());
    }


    @MessageMapping("/startGame")
    public void handleStartGame(Map<String, String> msg) {
        String gameId = msg.get("gameId");
        Game game = gameRepository.findById(gameId).orElse(null);

        if (game != null) {
            game.setStarted(true);
            gameRepository.save(game);

            messagingTemplate.convertAndSend("/topic/start/" + gameId, "\"start\"");
        }

    }



    @MessageMapping("/rollDice")
    public void rollDice(Map<String, String> msg) {
        String gameId = msg.get("gameId");

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return;
        
        int steps = new Random().nextInt(6) + 1;

        PlayerState currPlayer = game.getPlayerStates().get(game.getTurnIndex());

        currPlayer.setPosition((currPlayer.getPosition() + steps) % 40);


        game.setTurnIndex((game.getTurnIndex() + 1) % game.getPlayerStates().size());
        gameRepository.save(game);

        messagingTemplate.convertAndSend("/topic/gameUpdates/" + gameId, game);

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
}
