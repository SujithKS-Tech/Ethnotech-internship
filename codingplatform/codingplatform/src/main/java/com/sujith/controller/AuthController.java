package com.sujith.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;
import com.sujith.model.User;
import com.sujith.repository.UserRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository repo;

    @PostMapping("/register")
    public User register(@RequestBody User user){
        user.setRole("USER");
        user.setScore(0);
        return repo.save(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user){
        User u = repo.findByUsername(user.getUsername());

        if(u != null && u.getPassword().equals(user.getPassword())){
            return "Login Success";
        }
        return "Invalid";
    }
}