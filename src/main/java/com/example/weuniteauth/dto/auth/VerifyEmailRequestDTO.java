package com.example.weuniteauth.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequestDTO(
        @NotBlank(message = "O código não pode estar vazio")
        String verificationToken
) {}
