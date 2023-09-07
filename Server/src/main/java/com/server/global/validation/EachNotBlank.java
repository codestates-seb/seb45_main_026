package com.server.global.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EachNotBlankValidator.class)
public @interface EachNotBlank {

    String message() default "List contains blank value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
