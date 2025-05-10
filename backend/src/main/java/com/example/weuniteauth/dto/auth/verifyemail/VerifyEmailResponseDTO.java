package com.example.weuniteauth.dto.auth.verifyemail;

import com.example.weuniteauth.dto.common.ExtendedTokenResponseDTO;

public record VerifyEmailResponseDTO(
        String username,
        boolean verified,
        String message,
        String accessToken,
        Long expiresIn
) {
    public static VerifyEmailResponseDTO from(ExtendedTokenResponseDTO extendedResponse) {
        return new VerifyEmailResponseDTO(
                extendedResponse.username(),
                extendedResponse.verified(),
                extendedResponse.message(),
                extendedResponse.accessToken(),
                extendedResponse.expiresIn()
        );
    }
}
