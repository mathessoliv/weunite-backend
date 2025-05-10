package com.example.weuniteauth.dto.auth.resetpassword;

import com.example.weuniteauth.dto.common.VerificationRequestDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyResetTokenRequestDTO(
        @NotBlank(message = "O código não pode estar vazio")
        @Size(min = 6, max = 6, message = "O código deve ter 6 caracteres")
        String verificationToken
) {

    public static VerifyResetTokenRequestDTO from(VerificationRequestDTO verificationRequest) {
        return new VerifyResetTokenRequestDTO(verificationRequest.verificationCode());
    }
}
