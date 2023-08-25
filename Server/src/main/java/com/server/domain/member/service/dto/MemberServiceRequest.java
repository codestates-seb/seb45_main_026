package com.server.domain.member.service.dto;

import lombok.Builder;
import lombok.Getter;

public class MemberServiceRequest {

	@Getter
	@Builder
	public static class Create {
		private String code;

		private String email;

		private String password;

		private String nickname;
	}
}
