package com.example.weuniteauth.dto;

import com.example.weuniteauth.domain.Comment;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record PostDTO(
        String id,
        String text,
        String image,
        List<LikeDTO> likes,
        List<CommentDTO> comments,
        Instant createdAt,
        Instant updatedAt,
        UserDTO user
) {

}
