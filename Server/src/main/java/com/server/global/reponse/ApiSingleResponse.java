package com.server.global.reponse;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiSingleResponse<T> {
	private T data;
	private int code;
	private String status;
	private String message;

	// public static <T> ApiSingleResponse<T> ok() {
	// 	return ApiSingleResponse
	// }
	//
	// public static <T> ApiSingleResponse<T> of() {
	// 	return ApiSingleResponse
	// }
}
