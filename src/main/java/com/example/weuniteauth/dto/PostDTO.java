package com.example.weuniteauth.dto;

import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.dto.post.CreatePostResponseDTO;

import java.time.Instant;

public record PostDTO(
        String message,
        CreatePostResponseDTO post,
        UserDTO user
) {

}
