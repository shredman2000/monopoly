package com.monopoly.backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monopoly.backend.models.User;
import com.monopoly.backend.repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public String hello() {
        return "Hello from /user!";
    }

    @PostMapping("/newUser")
    public String createUser(@RequestBody User user) {
        userRepository.save(user);
        return "User created";
    }

    @GetMapping
    public List<User> getUsers() {
        return userRepository.findAll();
    }

}
