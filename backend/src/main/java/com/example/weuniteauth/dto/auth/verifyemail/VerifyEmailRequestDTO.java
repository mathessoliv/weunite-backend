package com.example.weuniteauth.dto.auth.verifyemail;

import com.example.weuniteauth.dto.common.VerificationRequestDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyEmailRequestDTO(
        @NotBlank(message = "O código não pode estar vazio")
        String verificationCode
) {
    public static VerifyEmailRequestDTO from(VerificationRequestDTO verificationRequest) {
        return new VerifyEmailRequestDTO(verificationRequest.verificationCode());
    }
}
