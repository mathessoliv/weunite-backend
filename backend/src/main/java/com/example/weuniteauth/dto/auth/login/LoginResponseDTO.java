package com.example.weuniteauth.dto.auth.login;

import com.example.weuniteauth.dto.common.TokenResponseDTO;

/**
 * Response DTO for login operations.
 * Extends the base TokenResponseDTO.
 */
public record LoginResponseDTO(
        String accessToken,
        Long expiresIn
) {
    /**
     * Creates a LoginResponseDTO from a TokenResponseDTO.
     */
    public static LoginResponseDTO from(TokenResponseDTO tokenResponse) {
        return new LoginResponseDTO(
                tokenResponse.accessToken(),
                tokenResponse.expiresIn()
        );
    }
}
