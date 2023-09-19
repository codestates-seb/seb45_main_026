package com.server.global.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageTypeValidator.class)
public @interface ImageTypeValid {
	String message() default "png, jpg, jpeg 타입의 확장자만 지원합니다";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
