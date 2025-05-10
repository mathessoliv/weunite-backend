package com.example.weuniteauth.dto.common;

import java.time.LocalDateTime;

public record UserBaseDTO(
        String id,
        String name,
        String username,
        String email,
        LocalDateTime createdAt
) {
}