package com.example.weuniteauth.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerificationRequestDTO(
        @NotBlank(message = "O código não pode estar vazio")
        @Size(min = 6, max = 6, message = "O código deve ter 6 caracteres")
        String verificationCode
) {
}