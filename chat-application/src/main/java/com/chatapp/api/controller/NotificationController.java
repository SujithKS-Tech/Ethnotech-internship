package com.chatapp.api.controller;

import com.chatapp.api.dto.NotificationResponse;
import com.chatapp.security.AppUserPrincipal;
import com.chatapp.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationResponse> getNotifications(@AuthenticationPrincipal AppUserPrincipal principal) {
        return notificationService.getNotifications(principal.getId());
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationResponse markAsRead(@PathVariable UUID notificationId,
                                           @AuthenticationPrincipal AppUserPrincipal principal) {
        return notificationService.markAsRead(notificationId, principal.getId());
    }
}
