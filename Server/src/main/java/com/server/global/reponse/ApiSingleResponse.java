package com.server.global.reponse;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.nimbusds.oauth2.sdk.ErrorResponse;
import com.server.global.exception.businessexception.BusinessException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.MissingServletRequestParameterException;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiSingleResponse<T> {
	private T data;
	private int code;
	private String status;
	private String message;

	public static <T> ApiSingleResponse<T> ok(T data) {
		return ApiSingleResponse.of(data, HttpStatus.OK);
	}

	public static <T> ApiSingleResponse<T> ok(T data, String message) {
		return ApiSingleResponse.of(data, HttpStatus.OK, message);
	}

	public static <T> ApiSingleResponse<T> of(T data, HttpStatus httpStatus) {
		return ApiSingleResponse.of(data, httpStatus, httpStatus.getReasonPhrase());
	}

	public static <T> ApiSingleResponse<T> of(T data, HttpStatus httpStatus, String message) {
		return new ApiSingleResponse<>(
			data,
			httpStatus.value(),
			httpStatus.name(),
			message);
	}

	public static ApiSingleResponse<Void> fail(BusinessException exception) {
		return new ApiSingleResponse<>(
			null,
			exception.getHttpStatus().value(),
			exception.getErrorCode(),
			exception.getMessage());
	}

	public static ApiSingleResponse<List<ErrorResponse>> fail(BindException exception) {
		return new ApiSingleResponse<>(
			ErrorResponse.of(exception.getFieldErrors()),
			HttpStatus.BAD_REQUEST.value(),
			HttpStatus.BAD_REQUEST.name(),
			"입력 값을 확인해주세요."
		);
	}

	public static ApiSingleResponse<List<ErrorResponse>> fail(ConstraintViolationException exception) {
		return new ApiSingleResponse<>(
			ErrorResponse.of(exception.getConstraintViolations()),
			HttpStatus.BAD_REQUEST.value(),
			HttpStatus.BAD_REQUEST.name(),
			"입력 값을 확인해주세요."
		);
	}

	public static ApiSingleResponse<List<ErrorResponse>> fail(MissingServletRequestParameterException exception) {
		return new ApiSingleResponse<>(
				List.of(ErrorResponse.of(
						exception.getParameterName(),
						"null",
						String.format("%s 값은 필수입니다.", exception.getParameterName())
				)),
				HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.name(),
				"입력 값을 확인해주세요."
		);
	}

	public static ApiSingleResponse<String> fail(HttpRequestMethodNotSupportedException exception) {
		return new ApiSingleResponse<>(
			String.format("지원하지 않는 Method : %s", exception.getMethod()),
			HttpStatus.METHOD_NOT_ALLOWED.value(),
			HttpStatus.METHOD_NOT_ALLOWED.name(),
			"요청 url 과 method 를 확인해주세요."
		);
	}

	public static ApiSingleResponse<Void> fail(Exception exception) {
		return new ApiSingleResponse<>(
			null,
			HttpStatus.INTERNAL_SERVER_ERROR.value(),
			HttpStatus.INTERNAL_SERVER_ERROR.name(),
			exception.getMessage()
		);
	}

	@Getter
	@AllArgsConstructor
	public static class ErrorResponse {

		private String field;
		private String value;
		private String reason;

		public static List<ErrorResponse> of(List<FieldError> fieldErrors) {

			return fieldErrors.stream().map(fieldError ->
				new ErrorResponse(
					fieldError.getField(),
					getRejectedValue(fieldError),
					fieldError.getDefaultMessage()
				)).collect(Collectors.toList());
		}

		private static String getRejectedValue(FieldError fieldError) {
			return Optional.ofNullable(fieldError.getRejectedValue()).orElse("null").toString();
		}

		public static List<ErrorResponse> of(Set<ConstraintViolation<?>> violations) {

			return violations.stream().map(violation -> new ErrorResponse(
				getField(violation),
				getInValidValue(violation),
				violation.getMessage()
			)).collect(Collectors.toList());
		}

		private static String getField(ConstraintViolation<?> violation) {
			return ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
		}

		private static String getInValidValue(ConstraintViolation<?> violation) {
			return Optional.ofNullable(violation.getInvalidValue()).orElse("null").toString();
		}

		public static ErrorResponse of(String field, String value, String reason) {
			return new ErrorResponse(field, value, reason);
		}

	}
}
