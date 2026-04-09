package com.chatapp.websocket;

import com.chatapp.service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketPresenceListener {

    private final PresenceService presenceService;

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        Authentication authentication = (Authentication) StompHeaderAccessor.wrap(event.getMessage()).getUser();
        if (authentication != null) {
            presenceService.markOnlineByEmail(authentication.getName());
        }
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        Authentication authentication = (Authentication) StompHeaderAccessor.wrap(event.getMessage()).getUser();
        if (authentication != null) {
            presenceService.markOfflineByEmail(authentication.getName());
        }
    }
}
