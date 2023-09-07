package com.server.global.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class EachPositiveValidator implements ConstraintValidator<EachPositive, List<? extends Number>> {

    @Override
    public void initialize(EachPositive constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<? extends Number> list, ConstraintValidatorContext context) {
        if (list == null) {
            return true;
        }

        for (Number item : list) {
            if (item == null || item.longValue() <= 0) {
                return false;
            }
        }
        return true;
    }
}

