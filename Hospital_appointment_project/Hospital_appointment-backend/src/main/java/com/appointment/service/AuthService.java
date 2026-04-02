package com.appointment.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.appointment.dtos.AuthResponse;
import com.appointment.dtos.LoginRequest;
import com.appointment.dtos.RegisterRequest;
import com.appointment.entities.User;
import com.appointment.repositories.UserRepository;
import com.appointment.security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    public String register(RegisterRequest request) {

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            return "User already exists with this email";
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());

        // encrypt password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setCreatedAt(LocalDateTime.now());
        user.setActiveStatus(true);

        userRepository.save(user);

        return "User registered successfully";
    }

    public AuthResponse login(LoginRequest request) {

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return new AuthResponse(null, "Invalid email");
        }

        User user = userOptional.get();

        boolean passwordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!passwordMatch) {
            return new AuthResponse(null, "Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(token, "Login successful");
    }

}
