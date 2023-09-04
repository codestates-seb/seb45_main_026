package com.server.domain.member.service.dto.request;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Builder;
import lombok.Getter;

public class MemberServiceRequest {

	@Getter
	@Builder
	public static class Nickname {
		private String nickname;
	}

	@Getter
	@Builder
	public static class Create {
		private String email;

		private String password;

		private String nickname;
	}

	@Getter
	@Builder
	public static class Password {
		private String prevPassword;
		private String newPassword;
	}
}
