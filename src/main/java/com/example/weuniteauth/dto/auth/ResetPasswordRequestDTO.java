package com.example.weuniteauth.dto.auth;

import com.example.weuniteauth.validations.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
        @NotBlank(message = "A nova senha não pode estar vazia")
        @ValidPassword
        String newPassword
) {
}
