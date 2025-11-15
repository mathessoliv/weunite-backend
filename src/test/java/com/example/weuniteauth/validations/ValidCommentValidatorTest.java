package com.example.weuniteauth.validations;

import com.example.weuniteauth.dto.comment.CommentRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValidCommentValidator Tests")
class ValidCommentValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // VALID COMMENT TESTS

    @Test
    @DisplayName("Should accept comment with valid text only")
    void validCommentWithTextOnly() {
        CommentRequestDTO dto = new CommentRequestDTO("This is a valid comment", null);

        Set<ConstraintViolation<CommentRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Comment with valid text should be valid");
    }

    @Test
    @DisplayName("Should accept comment with image only")
    void validCommentWithImageOnly() {
        CommentRequestDTO dto = new CommentRequestDTO(null, "http://image.url/photo.jpg");

        Set<ConstraintViolation<CommentRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Comment with image should be valid");
    }

    @Test
    @DisplayName("Should accept comment with both text and image")
    void validCommentWithTextAndImage() {
        CommentRequestDTO dto = new CommentRequestDTO("Great photo!", "http://image.url/photo.jpg");

        Set<ConstraintViolation<CommentRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Comment with text and image should be valid");
    }

    @Test
    @DisplayName("Should accept comment with long text")
    void validCommentWithLongText() {
        String longText = "A".repeat(500);
        CommentRequestDTO dto = new CommentRequestDTO(longText, null);

        Set<ConstraintViolation<CommentRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    // INVALID COMMENT TESTS

    @Test
    @DisplayName("Should reject comment with both null text and null image")
    void invalidCommentBothNull() {
        CommentRequestDTO dto = new CommentRequestDTO(null, null);

        Set<ConstraintViolation<CommentRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Comment with both null should be invalid");
    }

    @Test
    @DisplayName("Should reject comment with blank text and no image")
    void invalidCommentBlankTextNoImage() {
        CommentRequestDTO dto = new CommentRequestDTO("   ", null);

        Set<ConstraintViolation<CommentRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Comment with blank text and no image should be invalid");
    }

    @Test
    @DisplayName("Should reject comment with empty text and no image")
    void invalidCommentEmptyTextNoImage() {
        CommentRequestDTO dto = new CommentRequestDTO("", null);

        Set<ConstraintViolation<CommentRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject comment with blank text and blank image")
    void invalidCommentBothBlank() {
        CommentRequestDTO dto = new CommentRequestDTO("   ", "   ");

        Set<ConstraintViolation<CommentRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject comment with null text and blank image")
    void invalidCommentNullTextBlankImage() {
        CommentRequestDTO dto = new CommentRequestDTO(null, "   ");

        Set<ConstraintViolation<CommentRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    // EDGE CASE TESTS

    @Test
    @DisplayName("Should accept comment with single character text")
    void validCommentSingleCharacter() {
        CommentRequestDTO dto = new CommentRequestDTO("a", null);

        Set<ConstraintViolation<CommentRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept comment with text containing only emoji")
    void validCommentOnlyEmoji() {
        CommentRequestDTO dto = new CommentRequestDTO("👍", null);

        Set<ConstraintViolation<CommentRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept comment with whitespace but valid image")
    void validCommentBlankTextValidImage() {
        CommentRequestDTO dto = new CommentRequestDTO("   ", "http://image.url/photo.jpg");

        Set<ConstraintViolation<CommentRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Comment with image should be valid even with blank text");
    }

    @Test
    @DisplayName("Should accept comment with null text but valid image")
    void validCommentNullTextValidImage() {
        CommentRequestDTO dto = new CommentRequestDTO(null, "http://image.url/photo.jpg");

        Set<ConstraintViolation<CommentRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}

