package com.example.weuniteauth.dto;

import com.example.weuniteauth.domain.Comment;
import com.example.weuniteauth.domain.Like;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public record PostDTO(
        String message,
        String id,
        String text,
        String image,
        Set<LikeDTO> likes,
        List<Comment> comments,
        Instant createdAt,
        Instant updatedAt,
        UserDTO user
) {

}
