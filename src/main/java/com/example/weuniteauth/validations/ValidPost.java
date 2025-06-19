package com.example.weuniteauth.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPostValidator.class)
public @interface ValidPost {
    String message() default "Either text or image must be present";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}