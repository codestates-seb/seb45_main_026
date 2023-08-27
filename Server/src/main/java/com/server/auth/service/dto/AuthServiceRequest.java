package com.server.auth.service.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;

public class AuthServiceRequest {

	@Getter
	@Builder
	public static class Send {
		private String email;
	}
	@Getter
	@Builder
	public static class Login {
		private String email;

		private String password;
	}

	@Getter
	@Builder
	public static class Reset {
		private String email;
		private String password;
	}
}
