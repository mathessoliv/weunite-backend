package com.example.weuniteauth.dto.auth.resetpassword;

import com.example.weuniteauth.dto.common.MessageResponseDTO;

public record ResetPasswordResponseDTO(
        String message
) {
    public static ResetPasswordResponseDTO from(MessageResponseDTO messageResponse) {
        return new ResetPasswordResponseDTO(messageResponse.message());
    }
}
