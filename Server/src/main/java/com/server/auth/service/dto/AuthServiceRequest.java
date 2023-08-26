package com.server.auth.service.dto;

import lombok.Builder;
import lombok.Getter;

public class AuthServiceRequest {
	@Getter
	@Builder
	public static class Login {
		private String email;

		private String password;
	}
}
