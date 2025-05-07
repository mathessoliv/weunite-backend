package com.example.weuniteauth.dto.user;

import java.time.LocalDateTime;

public record UserResponseDTO(
        String id,
        String name,
        String username,
        String email,
        LocalDateTime createdAt
) {
}

