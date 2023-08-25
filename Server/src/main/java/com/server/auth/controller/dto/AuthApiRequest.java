package com.server.auth.controller.dto;

import javax.validation.constraints.NotNull;

import com.server.domain.member.service.dto.MemberServiceRequest;

import lombok.Getter;

public class AuthApiRequest {
	@Getter
	public static class Send {
		@NotNull
		private String email;
	}

	@Getter
	public static class Confirm {
		@NotNull
		private String email;

		@NotNull
		private String code;
	}

	@Getter
	public static class SignUp {
		@NotNull
		private String email;

		@NotNull
		private String password;

		@NotNull
		private String nickname;

		public MemberServiceRequest.Create toServiceRequest() {
			return MemberServiceRequest.Create.builder()
				.email(email)
				.password(password)
				.nickname(nickname)
				.build();
		}
	}
}
