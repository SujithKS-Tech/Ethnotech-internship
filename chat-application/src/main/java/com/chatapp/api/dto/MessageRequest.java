package com.chatapp.api.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(@NotBlank String content) {
}
