package com.example.weuniteauth.dto;

import java.time.Instant;
import java.util.List;

public record CommentDTO(

        String message,

        String id,

        UserDTO author,

        PostDTO post,

        CommentDTO parentComment,

        List<CommentDTO> comments,

        String text,

        String image,

        Instant createdAt,

        Instant updatedAt

) {
}
