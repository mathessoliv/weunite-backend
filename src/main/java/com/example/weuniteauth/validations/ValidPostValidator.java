package com.example.weuniteauth.validations;

import com.example.weuniteauth.dto.post.CreatePostRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPostValidator implements ConstraintValidator<ValidPost, CreatePostRequestDTO> {
    @Override
    public boolean isValid(CreatePostRequestDTO dto, ConstraintValidatorContext context) {
        return (dto.text() != null && !dto.text().isBlank()) ||
                (dto.image() != null && !dto.image().isBlank());
    }
}