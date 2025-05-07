package com.example.weuniteauth.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 40;
    private static final String SYMBOL_REGEX = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?~].*";
    private static final String LOWERCASE_REGEX = ".*[a-z].*";
    private static final String UPPERCASE_REGEX = ".*[A-Z].*";
    private static final String DIGIT_REGEX = ".*\\d.*";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        if (password == null) {
            return false;
        }

        List<String> violations = new ArrayList<>();

        if (password.length() < MIN_LENGTH) {
            violations.add("A senha deve ter no minímo" + MIN_LENGTH + " caracteres");
        }

        if (password.length() > MAX_LENGTH) {
            violations.add("A senha deve ter no máximo " + MAX_LENGTH + " caracteres");
        }

        if (!password.matches(SYMBOL_REGEX)) {
            violations.add("A senha deve ter no minímo um símbolo (ex: !@#$%^&*)");
        }

        if (!password.matches(LOWERCASE_REGEX)) {
            violations.add("A senha deve ter no minímo uma letra minúscula");
        }

        if (!password.matches(UPPERCASE_REGEX)) {
            violations.add("A senha deve ter no minímo uma letra maiúscula");
        }

        if (!password.matches(DIGIT_REGEX)) {
            violations.add("A senha deve ter no minímo um número");
        }

        if (!violations.isEmpty()){
            context.disableDefaultConstraintViolation();

            for (String violation : violations) {
                context.buildConstraintViolationWithTemplate(violation)
                        .addConstraintViolation();
            }
            return false;
        }
        return true;
    }
}
