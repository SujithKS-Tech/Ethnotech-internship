package com.chatapp.service;

import com.chatapp.api.dto.AuthRequest;
import com.chatapp.api.dto.AuthResponse;
import com.chatapp.api.dto.RegisterRequest;
import com.chatapp.domain.model.Role;
import com.chatapp.domain.model.User;
import com.chatapp.domain.repository.UserRepository;
import com.chatapp.security.AppUserPrincipal;
import com.chatapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already in use");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(email);
        user.setDisplayName(request.displayName());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);

        AppUserPrincipal principal = new AppUserPrincipal(savedUser);
        return new AuthResponse(jwtService.generateToken(principal), savedUser.getId(), savedUser.getUsername(), savedUser.getDisplayName());
    }

    public AuthResponse login(AuthRequest request) {
        String email = request.email().toLowerCase();
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, request.password())
        );

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        AppUserPrincipal principal = new AppUserPrincipal(user);
        return new AuthResponse(jwtService.generateToken(principal), user.getId(), user.getUsername(), user.getDisplayName());
    }
}
