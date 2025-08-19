package com.example.weuniteauth.dto;

import java.time.Instant;

public record UserDTO(
        String id,
        String name,
        String username,
        String bio,
        String email,
        String profileImg,
        boolean isPrivate,
        Instant createdAt,
        Instant updatedAt
) {
}
