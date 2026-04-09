package com.chatapp.api.controller;

import com.chatapp.api.dto.UserSummaryResponse;
import com.chatapp.security.AppUserPrincipal;
import com.chatapp.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserSummaryResponse> getUsers(@AuthenticationPrincipal AppUserPrincipal principal) {
        return userService.getAvailableUsers(principal.getId());
    }
}
