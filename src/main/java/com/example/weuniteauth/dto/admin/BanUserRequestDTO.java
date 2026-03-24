package com.example.weuniteauth.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BanUserRequestDTO(
        @NotNull(message = "ID do usuário é obrigatório")
        Long userId,

        @NotNull(message = "ID do admin é obrigatório")
        Long adminId,

        @NotBlank(message = "Motivo do banimento é obrigatório")
        @Size(min = 10, max = 500, message = "Motivo deve ter entre 10 e 500 caracteres")
        String reason,

        Long reportId // ID da denúncia relacionada (opcional, mas útil para rastreabilidade)
) {
}
