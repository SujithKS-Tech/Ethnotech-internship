package com.chatapp.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateMessageRequest(@NotBlank String content) {
}
