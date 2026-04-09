package com.chatapp.api.controller;

import com.chatapp.api.dto.PresenceResponse;
import com.chatapp.service.PresenceService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/presence")
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

    @GetMapping("/{userId}")
    public PresenceResponse getPresence(@PathVariable UUID userId) {
        return presenceService.getPresence(userId);
    }
}
