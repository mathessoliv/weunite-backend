package com.example.weuniteauth.dto;

public record AuthDTO(
        String message,
        UserDTO user,
        String jwt,
        Long expiresIn
) { }
