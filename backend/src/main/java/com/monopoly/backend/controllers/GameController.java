package com.monopoly.backend.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monopoly.backend.models.Game;
import com.monopoly.backend.repository.GameRepository;
import com.monopoly.backend.services.GameCreatedMessage;
import com.monopoly.backend.services.GameService;
import com.monopoly.backend.services.GameSettings;
import com.monopoly.backend.services.GameStateResponse;
import com.monopoly.backend.services.JoinGameRequest;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/games")
public class GameController {

    private GameRepository gameRepository;
    private GameService gameService;

    // dependency injector
    public GameController(GameRepository gameRepository, GameService gameService) {
        this.gameRepository = gameRepository;
        this.gameService = gameService;

    }
    

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/createGame")
    public ResponseEntity<GameCreatedMessage> createGame(@RequestBody GameSettings gameSettings) {
        String hostUsername = gameSettings.getPlayerUsernames().get(0);

        // check for the right number of players being passed, failsafe
        Game game = gameService.createNewGame(hostUsername);

        gameRepository.save(game);

        String createdGameId = game.getGameId();
        
        return ResponseEntity.ok(new GameCreatedMessage(createdGameId, "Game Created Successfully"));
    }



    //@CrossOrigin(origins = "http://localhost:3000/")
    @PostMapping("/joinGame")
    public ResponseEntity joinGame(@RequestBody JoinGameRequest joinGameRequest) {
        String username = joinGameRequest.getUsername();
        String gameId = joinGameRequest.getGameId();
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Game not found.");
        }
        Game game = gameOpt.get();
        //check whether user already in game
        List<String> joinedPlayers = game.getPlayerUsernames();
        for (String joinedUsername : joinedPlayers) {
            if (username.equals(joinedUsername)) {
                return ResponseEntity.badRequest().body("Player already in game.");
            }
        }
        if (game.getNumPlayers() > 5) {
            System.out.println("Lobby full");
            return ResponseEntity.badRequest().body("Lobby Full");
        }
        game.addPlayer(username);
        //game.setNumPlayers(joinedPlayers.size()); test whether necessary
        gameRepository.save(game);
        return ResponseEntity.ok().body("Game joined successfully");
    }



    @PostMapping("/startGame")
    public ResponseEntity startGame(@RequestBody Map<String, String> request) {
        String gameId = request.get("gameId");
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("gameId does not exist");
        }
        Game game = gameOpt.get();
        game.setStarted(true);
        gameRepository.save(game);
        return ResponseEntity.ok("Game started");
    }

    


    /**
     * @return
     * players positions
     * turn # or which player
     * each players balance
     * PROBABLY IS USING WEBSOCKET, MAY BE ABLE TO REMOVE THIS
     */
    @GetMapping("/getGameState/{gameId}")
    public ResponseEntity<?> getGameState(@PathVariable String gameId) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);

        if (gameOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Game not found.");
        }

        Game game = gameOpt.get();
    
        GameStateResponse gameState = new GameStateResponse(
            game.getGameId(),
            game.isStarted(),
            game.getTurnIndex(),
            game.getPlayerUsernames(),
            game.getPlayerStates()
        );


        return ResponseEntity.ok(gameState);
    }
}