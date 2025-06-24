package com.example.weuniteauth.dto;


public record LikeDTO(
        String message,
        String id,
        String user,
        PostDTO post
) {
}
