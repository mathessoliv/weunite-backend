package com.example.weuniteauth.validations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValidPasswordValidator Tests")
class ValidPasswordValidatorTest {

    private Validator validator;

    // Classe de teste para validar a anotação
    static class TestPassword {
        @ValidPassword
        private String password;

        public TestPassword(String password) {
            this.password = password;
        }
    }

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // VALID PASSWORD TESTS

    @Test
    @DisplayName("Should accept valid password with all requirements")
    void validPasswordAllRequirements() {
        TestPassword test = new TestPassword("ValidPass123!");

        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);

        assertTrue(violations.isEmpty(), "Password should be valid");
    }

    @Test
    @DisplayName("Should accept password with minimum length")
    void validPasswordMinLength() {
        TestPassword test = new TestPassword("Pass123!");

        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);

        assertTrue(violations.isEmpty(), "Password with 8 characters should be valid");
    }

    @Test
    @DisplayName("Should accept password with maximum length")
    void validPasswordMaxLength() {
        TestPassword test = new TestPassword("ValidPassword123!@#$%^&*()Ab");

        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);

        assertTrue(violations.isEmpty(), "Password with 30 characters should be valid");
    }

    @Test
    @DisplayName("Should accept password with multiple symbols")
    void validPasswordMultipleSymbols() {
        TestPassword test = new TestPassword("Pass123!@#$%");

        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);

        assertTrue(violations.isEmpty());
    }

    // INVALID PASSWORD TESTS - NULL

    @Test
    @DisplayName("Should reject null password")
    void invalidPasswordNull() {
        TestPassword test = new TestPassword(null);

        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);

        assertFalse(violations.isEmpty(), "Null password should be invalid");
    }

    // INVALID PASSWORD TESTS - LENGTH

    @Test
    @DisplayName("Should reject password shorter than 8 characters")
    void invalidPasswordTooShort() {
        TestPassword test = new TestPassword("Pass1!");

        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("no mínimo 8 caracteres")));
    }

    @Test
    @DisplayName("Should reject password longer than 30 characters")
    void invalidPasswordTooLong() {
        TestPassword test = new TestPassword("ValidPassword123!@#$%^&*()Abcdefghijk");

        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("no máximo 30 caracteres")));
    }

    // INVALID PASSWORD TESTS - MISSING REQUIREMENTS

    @Test
    @DisplayName("Should reject password without symbol")
    void invalidPasswordNoSymbol() {
        TestPassword test = new TestPassword("Password123");

        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("no mínimo um símbolo")));
    }

    @Test
    @DisplayName("Should reject password without lowercase letter")
    void invalidPasswordNoLowercase() {
        TestPassword test = new TestPassword("PASSWORD123!");

        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("no mínimo uma letra minúscula")));
    }

    @Test
    @DisplayName("Should reject password without uppercase letter")
    void invalidPasswordNoUppercase() {
        TestPassword test = new TestPassword("password123!");

        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("no mínimo uma letra maiúscula")));
    }

    @Test
    @DisplayName("Should reject password without digit")
    void invalidPasswordNoDigit() {
        TestPassword test = new TestPassword("Password!");

        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("no mínimo um número")));
    }

    // MULTIPLE VIOLATIONS TESTS

    @Test
    @DisplayName("Should report multiple violations")
    void invalidPasswordMultipleViolations() {
        TestPassword test = new TestPassword("pass"); // too short, no uppercase, no digit, no symbol

        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);

        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 3, "Should have multiple violations");
    }

    @Test
    @DisplayName("Should reject password with only lowercase")
    void invalidPasswordOnlyLowercase() {
        TestPassword test = new TestPassword("password");

        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);

        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 3); // missing uppercase, digit, and symbol
    }

    // EDGE CASE TESTS

    @Test
    @DisplayName("Should accept password with special characters in different positions")
    void validPasswordSymbolAtEnd() {
        TestPassword test = new TestPassword("Password123!");

        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept password with symbol at start")
    void validPasswordSymbolAtStart() {
        TestPassword test = new TestPassword("!Password123");

        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept various symbol types")
    void validPasswordVariousSymbols() {
        String[] passwords = {
                "Pass123!",
                "Pass123@",
                "Pass123#",
                "Pass123$",
                "Pass123%",
                "Pass123^",
                "Pass123&",
                "Pass123*"
        };

        for (String pwd : passwords) {
            TestPassword test = new TestPassword(pwd);
            Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);
            assertTrue(violations.isEmpty(), "Password '" + pwd + "' should be valid");
        }
    }
}

