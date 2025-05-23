package com.example.weuniteauth.dto;

import java.time.Instant;

public record UserDTO(
        String message,
        String id,
        String name,
        String username,
        String bio,
        String email,
        String profileImg,
        Instant createdAt,
        Instant updatedAt
) {
}
