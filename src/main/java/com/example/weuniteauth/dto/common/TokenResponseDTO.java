package com.example.weuniteauth.dto.common;

public record TokenResponseDTO(
        String accessToken,
        Long expiresIn
) {
}