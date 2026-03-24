package com.example.weuniteauth.dto.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SuspendUserRequestDTO(
        @NotNull(message = "ID do usuário é obrigatório")
        Long userId,

        @NotNull(message = "ID do admin é obrigatório")
        Long adminId,

        @NotNull(message = "Duração da suspensão é obrigatória")
        @Min(value = 1, message = "Duração deve ser no mínimo 1 dia")
        Integer durationInDays,

        @NotBlank(message = "Motivo da suspensão é obrigatório")
        @Size(min = 10, max = 500, message = "Motivo deve ter entre 10 e 500 caracteres")
        String reason,

        Long reportId // ID da denúncia relacionada (opcional)
) {
}
