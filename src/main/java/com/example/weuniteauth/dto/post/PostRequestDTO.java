package com.example.weuniteauth.dto.post;

import com.example.weuniteauth.validations.ValidPost;
import jakarta.validation.constraints.NotNull;

@ValidPost
public record PostRequestDTO(
    @NotNull
    Long authorId,

    String text,

    String image

) {
}
