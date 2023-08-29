package com.server.domain.member.controller.dto;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import com.server.domain.member.service.dto.request.MemberServiceRequest;
import com.server.module.s3.service.dto.ImageType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberApiRequest {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Nickname {
		@NotBlank
		private String nickname;

		public MemberServiceRequest.Nickname toServiceRequest() {
			return MemberServiceRequest.Nickname.builder()
				.nickname(nickname)
				.build();
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Image {
		private String imageName;
		private ImageType imageType;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
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
