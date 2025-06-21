package com.example.weuniteauth.dto.post;

import com.example.weuniteauth.validations.ValidPost;
import jakarta.validation.constraints.NotNull;

@ValidPost
public record PostRequestDTO(

        String text,

        String image

) {
}
