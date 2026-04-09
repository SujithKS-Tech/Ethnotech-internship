package com.chatapp.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Size(min = 3, max = 50) String username,
    @Email @NotBlank String email,
    @NotBlank @Size(min = 2, max = 100) String displayName,
    @NotBlank @Size(min = 6, max = 100) String password
) {
}
