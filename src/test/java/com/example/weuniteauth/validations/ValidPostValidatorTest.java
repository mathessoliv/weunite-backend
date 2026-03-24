package com.example.weuniteauth.validations;

import com.example.weuniteauth.dto.post.PostRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValidPostValidator Tests")
class ValidPostValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // VALID POST TESTS

    @Test
    @DisplayName("Should accept post with valid text")
    void validPostWithText() {
        PostRequestDTO dto = new PostRequestDTO("This is a valid post");

        Set<ConstraintViolation<PostRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Post with valid text should be valid");
    }

    @Test
    @DisplayName("Should accept post with long text")
    void validPostWithLongText() {
        String longText = "A".repeat(1000);
        PostRequestDTO dto = new PostRequestDTO(longText);

        Set<ConstraintViolation<PostRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept post with text containing special characters")
    void validPostWithSpecialCharacters() {
        PostRequestDTO dto = new PostRequestDTO("Post with emojis 🎉 and symbols!@#$%");

        Set<ConstraintViolation<PostRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    // INVALID POST TESTS

    @Test
    @DisplayName("Should reject post with null text")
    void invalidPostNullText() {
        PostRequestDTO dto = new PostRequestDTO(null);

        Set<ConstraintViolation<PostRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Post with null text should be invalid");
    }

    @Test
    @DisplayName("Should reject post with empty text")
    void invalidPostEmptyText() {
        PostRequestDTO dto = new PostRequestDTO("");

        Set<ConstraintViolation<PostRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Post with empty text should be invalid");
    }

    @Test
    @DisplayName("Should reject post with blank text")
    void invalidPostBlankText() {
        PostRequestDTO dto = new PostRequestDTO("   ");

        Set<ConstraintViolation<PostRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Post with blank text should be invalid");
    }

    @Test
    @DisplayName("Should reject post with only whitespace")
    void invalidPostOnlyWhitespace() {
        PostRequestDTO dto = new PostRequestDTO("\n\t  \n");

        Set<ConstraintViolation<PostRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    // EDGE CASE TESTS

    @Test
    @DisplayName("Should accept post with single character")
    void validPostSingleCharacter() {
        PostRequestDTO dto = new PostRequestDTO("a");

        Set<ConstraintViolation<PostRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept post with trimmed text")
    void validPostTrimmedText() {
        PostRequestDTO dto = new PostRequestDTO("  Valid text with spaces  ");

        Set<ConstraintViolation<PostRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}

