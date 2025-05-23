package com.example.weuniteauth.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDTO(
        @NotBlank(message = "O nome não pode estar vazio")
        @Size(min = 5, max = 100, message = "O nome deve conter entre 5 e 100 caracteres")
        String name,

        @NotBlank(message = "O nome de usuário não pode estar vazio")
        @Size(min = 5, max = 30, message = "O nome de usuário deve conter entre 5 e 30 caracteres")
        String username,

        @Size(max = 500, message = "A bio deve conter no máximo 500 caracteres")
        String bio
        ) {
}
