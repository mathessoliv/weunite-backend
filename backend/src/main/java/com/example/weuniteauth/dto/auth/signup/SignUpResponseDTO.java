package com.example.weuniteauth.dto.auth.signup;

import com.example.weuniteauth.dto.common.UserBaseDTO;

import java.time.LocalDateTime;


public record SignUpResponseDTO(
        String id,
        String name,
        String username,
        String email,
        LocalDateTime createdAt
) {
    public static SignUpResponseDTO from(UserBaseDTO userBase) {
        return new SignUpResponseDTO(
                userBase.id(),
                userBase.name(),
                userBase.username(),
                userBase.email(),
                userBase.createdAt()
        );
    }
}
