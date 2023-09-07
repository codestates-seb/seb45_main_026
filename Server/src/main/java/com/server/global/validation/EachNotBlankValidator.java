package com.server.global.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class EachNotBlankValidator implements ConstraintValidator<EachNotBlank, List<?>> {

    @Override
    public void initialize(EachNotBlank constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<?> list, ConstraintValidatorContext context) {
        if (list == null) {
            return true;
        }

        for (Object item : list) {
            if (item == null || item.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}

