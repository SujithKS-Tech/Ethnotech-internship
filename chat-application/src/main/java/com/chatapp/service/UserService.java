package com.chatapp.service;

import com.chatapp.api.dto.UserSummaryResponse;
import com.chatapp.domain.model.User;
import com.chatapp.domain.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getById(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public List<UserSummaryResponse> getAvailableUsers(UUID currentUserId) {
        return userRepository.findByIdNot(currentUserId).stream()
            .map(user -> new UserSummaryResponse(user.getId(), user.getUsername(), user.getDisplayName(), user.isOnline()))
            .toList();
    }
}
