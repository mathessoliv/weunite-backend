package com.example.weuniteauth.dto.user;

import com.example.weuniteauth.dto.common.UserBaseDTO;

import java.time.LocalDateTime;

/**
 * Response DTO for user information.
 * Uses the UserBaseDTO structure.
 */
public record UserResponseDTO(
        String id,
        String name,
        String username,
        String email,
        LocalDateTime createdAt
) {
    /**
     * Creates a UserResponseDTO from a UserBaseDTO.
     */
    public static UserResponseDTO from(UserBaseDTO userBase) {
        return new UserResponseDTO(
                userBase.id(),
                userBase.name(),
                userBase.username(),
                userBase.email(),
                userBase.createdAt()
        );
    }
}
