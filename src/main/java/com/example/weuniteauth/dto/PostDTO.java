package com.example.weuniteauth.dto;

import java.time.Instant;
import java.util.List;

public record PostDTO(
        String id,
        String text,
        String imageUrl,
        String videoUrl,
        List<LikeDTO> likes,
        List<CommentDTO> comments,
        List<RepostDTO> reposts,
        Instant createdAt,
        Instant updatedAt,
        UserDTO user,
        UserDTO repostedBy,
        Instant repostedAt
) {

}
