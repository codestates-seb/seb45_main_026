package com.server.auth.controller.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.server.auth.oauth.service.OAuthProvider;
import com.server.auth.service.dto.AuthServiceRequest;
import com.server.domain.member.service.dto.request.MemberServiceRequest;
import com.server.module.email.service.dto.MailServiceRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AuthApiRequest {
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Send {
		@NotBlank
		private String email;

		public AuthServiceRequest.Send toServiceRequest() {
			return AuthServiceRequest.Send.builder()
				.email(email)
				.build();
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Confirm {
		@NotBlank
		private String email;

		@NotBlank
		private String code;

		public MailServiceRequest.Confirm toServiceRequest() {
			return MailServiceRequest.Confirm.builder()
				.email(email)
				.code(code)
				.build();
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SignUp {
		@NotBlank
		@Email(message = "이메일 양식을 확인하세요.")
		private String email;

		@NotBlank
		private String password;

		@NotBlank
		private String nickname;

		public MemberServiceRequest.Create toServiceRequest() {
			return MemberServiceRequest.Create.builder()
				.email(email)
				.password(password)
				.nickname(nickname)
				.build();
		}
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Login {
		private String email;
		private String password;
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Token {
		private String accessToken;
		private String refreshToken;
		private Long memberId;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OAuth {
		private OAuthProvider provider;
		private String code;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Reset {
		@NotBlank
		@Email
		private String email;
		@NotBlank
		private String password;

		public AuthServiceRequest.Reset toServiceRequest() {
			return AuthServiceRequest.Reset.builder()
				.email(email)
				.password(password)
				.build();
		}
	}
}
