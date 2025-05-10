package com.example.weuniteauth.dto.auth.resetpassword;

import com.example.weuniteauth.validations.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
        @NotBlank(message = "A nova senha não pode estar vazia")
        @ValidPassword
        String newPassword,
        @NotBlank(message = "O código não pode estar vazio")
        @Size(min = 6, max = 6, message = "O código deve ter 6 caracteres")
        String verificationToken
) {
}
