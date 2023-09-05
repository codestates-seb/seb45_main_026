package com.server.auth.service.dto;

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

	@Getter
	@Builder
	public static class Confirm {
		private String email;

		private String code;
	}
}
