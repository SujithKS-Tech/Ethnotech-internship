package com.chatapp.api.controller;

import com.chatapp.api.dto.MessageResponse;
import com.chatapp.api.dto.TypingEventRequest;
import com.chatapp.api.dto.TypingEventResponse;
import com.chatapp.api.dto.WebSocketChatRequest;
import com.chatapp.domain.model.User;
import com.chatapp.service.ChatService;
import com.chatapp.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Valid @Payload WebSocketChatRequest request, Principal principal) {
        MessageResponse response = chatService.sendMessage(
            request.chatRoomId(),
            request.content(),
            userService.getByEmail(principal.getName())
        );

        messagingTemplate.convertAndSend("/topic/chat." + request.chatRoomId(), response);
    }

    @MessageMapping("/chat.typing")
    public void typing(@Valid @Payload TypingEventRequest request, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        chatService.assertMembership(request.chatRoomId(), user.getId());
        messagingTemplate.convertAndSend(
            "/topic/chat.typing." + request.chatRoomId(),
            new TypingEventResponse(request.chatRoomId(), user.getId(), user.getDisplayName(), request.typing())
        );
    }
}
