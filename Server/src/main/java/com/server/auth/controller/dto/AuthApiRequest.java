package com.server.auth.controller.dto;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.server.auth.oauth.service.OAuthProvider;
import com.server.auth.service.dto.AuthServiceRequest;
import com.server.domain.member.service.dto.request.MemberServiceRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AuthApiRequest {
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Send {
		@NotNull(message = "{validation.not-blank}")
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
		@NotNull(message = "{validation.not-blank}")
		@Email(message = "{validation.auth.email}")
		private String email;

		@NotNull(message = "{validation.auth.code}")
		private String code;

		public AuthServiceRequest.Confirm toServiceRequest() {
			return AuthServiceRequest.Confirm.builder()
				.email(email)
				.code(code)
				.build();
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SignUp {
		@NotNull(message = "{validation.not-blank}")
		@Email(message = "{validation.auth.email}")
		private String email;

		@NotNull(message = "{validation.auth.password}")
		@Size(min = 9, max = 20, message = "{validation.size}")
		@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$", message = "{validation.auth.password}")
		private String password;

		@NotNull(message = "{validation.auth.nickname}")
		@Size(min = 1, max = 20, message = "{validation.size}")
		@Pattern(regexp = "^[a-zA-Z0-9가-힣]{1,20}$", message = "{validation.auth.nickname}")
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
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Login {
		@NotNull(message = "{validation.not-blank}")
		@Email(message = "{validation.auth.email}")
		private String email;

		@NotNull(message = "{validation.not-blank}")
		@Size(min = 9, max = 20, message = "{validation.size}")
		@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$", message = "{validation.auth.password}")
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

		@NotNull(message = "{validation.auth.code}")
		private String code;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Reset {
		@NotNull(message = "{validation.not-blank}")
		@Email(message = "{validation.auth.email}")
		private String email;

		@NotNull(message = "{validation.not-blank}")
		@Size(min = 9, max = 20, message = "{validation.size}")
		@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$", message = "{validation.auth.password}")
		private String password;

		public AuthServiceRequest.Reset toServiceRequest() {
			return AuthServiceRequest.Reset.builder()
				.email(email)
				.password(password)
				.build();
		}
	}
}
