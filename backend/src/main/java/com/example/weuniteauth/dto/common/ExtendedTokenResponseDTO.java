package com.example.weuniteauth.dto.common;

public record ExtendedTokenResponseDTO(
        String username,
        boolean verified,
        String message,
        String accessToken,
        Long expiresIn
) {
}