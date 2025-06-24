package com.example.weuniteauth.dto;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record PostDTO(
        String message,
        String id,
        String text,
        String image,
        Set<String> likes,
        List<String> comments,
        Instant createdAt,
        Instant updatedAt,
        UserDTO user
) {

}
