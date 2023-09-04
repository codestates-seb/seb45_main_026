package com.server.global.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import com.server.module.s3.service.dto.ImageType;

public class ImageTypeValidator implements ConstraintValidator<ImageTypeValid, ImageType> {

	@Override
	public boolean isValid(ImageType value, ConstraintValidatorContext context) {
		if (value == null) {
			return false;
		}

		try {
			ImageType.valueOf(value.name().toUpperCase());
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
