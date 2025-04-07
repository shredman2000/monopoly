package com.monopoly.backend.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monopoly.backend.models.Game;
import com.monopoly.backend.models.User;
import com.monopoly.backend.repository.GameRepository;
import com.monopoly.backend.repository.UserRepository;
import com.monopoly.backend.services.GameCreatedMessage;
import com.monopoly.backend.services.GameSettings;

import com.monopoly.backend.services.JoinGameRequest;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/games")
public class GameController {

    private GameRepository gameRepository;
    private UserRepository userRepository;

    // dependency injector
    public GameController(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }


    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/createGame")
    public ResponseEntity<GameCreatedMessage> createGame(@RequestBody GameSettings gameSettings) {
        List<User> players = userRepository.findAllByUsernameIn(gameSettings.getPlayerUsernames());

        // check for the right number of players being passed, failsafe

        Game game = new Game(players);
        gameRepository.save(game);

        String createdGameId = game.getGameId();
        
        return ResponseEntity.ok(new GameCreatedMessage(createdGameId, "Game Created Successfully"));
    }

    //@CrossOrigin(origins = "http://localhost:3000/")
    @PostMapping("/joinGame")
    public ResponseEntity joinGame(@RequestBody JoinGameRequest joinGameRequest) {
        String username = joinGameRequest.getUsername();
        String gameId = joinGameRequest.getGameId();

        /* TODO: NEED TO IMPLEMENT LOGIN / SIGNUP
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }*/

       User user = userRepository.findByUsername(username)
            .orElseGet(() -> userRepository.save(new User(username , null)));

        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Game not found.");
        }

        Game game = gameOpt.get();


        //check whether user already in game
        boolean alreadyJoined = game.getPlayers().stream()
        .anyMatch(p -> p.getId().equals(user.getId()));


        game.getPlayers().add(user);
        game.getPlayerUsernames().add(user.getUsername());
        game.setNumPlayers(game.getPlayers().size());
        gameRepository.save(game);

        return ResponseEntity.ok().body("Game joined successfully");
    }
}