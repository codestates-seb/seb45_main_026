package com.server.auth.controller.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
		@NotBlank(message = "{validation.notblank}")
		@Email(message = "{validation.auth.email}")
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
		@NotBlank(message = "{validation.notblank}")
		@Email(message = "{validation.auth.email}")
		private String email;

		@NotBlank(message = "{validation.auth.code}")
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
		@NotBlank(message = "{validation.notblank}")
		@Email(message = "{validation.auth.email}")
		private String email;

		@NotBlank(message = "{validation.auth.password}")
		private String password;

		@NotBlank(message = "{validation.auth.nickname}")
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
		@NotBlank(message = "{validation.notblank}")
		@Email(message = "{validation.auth.email}")
		private String email;
		@NotBlank(message = "{validation.notblank}")
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
		@NotNull(message = "{validation.auth.provider}")
		private OAuthProvider provider;
		private String code;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Reset {
		@NotBlank(message = "{validation.notblank}")
		@Email(message = "{validation.auth.email}")
		private String email;
		@NotBlank(message = "{validation.notblank}")
		private String password;

		public AuthServiceRequest.Reset toServiceRequest() {
			return AuthServiceRequest.Reset.builder()
				.email(email)
				.password(password)
				.build();
		}
	}
}
