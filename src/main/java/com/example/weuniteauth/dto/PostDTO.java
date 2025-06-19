package com.example.weuniteauth.dto;

import com.example.weuniteauth.domain.Comment;
import com.example.weuniteauth.domain.Like;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Set;

@JsonInclude(JsonInclude.Include.ALWAYS)
public record PostDTO(
        String message,
        String id,
        String text,
        String image,
        Set<Like> likes,
        ArrayList<Comment> comments,
        Instant createdAt,
        Instant updatedAt,
        UserDTO user
) {

}
