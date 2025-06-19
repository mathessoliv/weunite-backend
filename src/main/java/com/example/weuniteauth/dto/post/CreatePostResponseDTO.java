package com.example.weuniteauth.dto.post;

import com.example.weuniteauth.domain.Comment;
import com.example.weuniteauth.domain.Like;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Set;

public record CreatePostResponseDTO(
        String authorId,
        String text,
        String image,
        Set<Like> likes,
        ArrayList<Comment> comments,
        Instant createdAt,
        Instant updatedAt
) {
}
