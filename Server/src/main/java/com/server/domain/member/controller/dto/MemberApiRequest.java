package com.server.domain.member.controller.dto;

import org.hibernate.validator.constraints.NotBlank;

import com.server.domain.member.service.dto.MemberServiceRequest;
import com.server.module.email.service.dto.MailServiceRequest;

import lombok.Getter;

public class MemberApiRequest {

	public static class Password {
		@NotBlank
		private String prevPassword;
		@NotBlank
		private String newPassword;

		private Long loginId;

		public void setLoginId(Long loginId) {
			this.loginId = loginId;
		}

		public MemberServiceRequest.Password toServiceRequest() {
			return MemberServiceRequest.Password.builder()
				.prevPassword(prevPassword)
				.newPassword(newPassword)
				.loginId(loginId)
				.build();
		}
	}
}
