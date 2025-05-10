package com.example.weuniteauth.dto.auth.resetpassword;

import com.example.weuniteauth.dto.common.MessageResponseDTO;

public record VerifyResetTokenResponseDTO(
        String message
) {
    public static VerifyResetTokenResponseDTO from(MessageResponseDTO messageResponse) {
        return new VerifyResetTokenResponseDTO(messageResponse.message());
    }
}
