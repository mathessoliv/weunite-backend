package com.example.weuniteauth.dto.auth.resetpassword;

import com.example.weuniteauth.dto.common.MessageResponseDTO;

public record SendResetPasswordResponseDTO(
        String message
) {
    public static SendResetPasswordResponseDTO from(MessageResponseDTO messageResponse) {
        return new SendResetPasswordResponseDTO(messageResponse.message());
    }
}
