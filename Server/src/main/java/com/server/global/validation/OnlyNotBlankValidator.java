package com.server.global.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class OnlyNotBlankValidator implements ConstraintValidator<OnlyNotBlank, Object> {

    @Override
    public void initialize(OnlyNotBlank constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object field, ConstraintValidatorContext context) {
        if (field == null) {
            return true;
        }

        return !field.toString().trim().isEmpty();
    }
}

