package com.example.weuniteauth.dto;

import java.time.Instant;

public record UserDTO(
        String id,
        String name,
        String username,
        String email,
        String profileImg,
        String jwt,
        Long expiresIn,
        Instant createdAt,
        Instant updatedAt
) {
}
