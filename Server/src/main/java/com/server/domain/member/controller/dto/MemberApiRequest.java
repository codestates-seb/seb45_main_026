package com.server.domain.member.controller.dto;

import javax.validation.constraints.NotBlank;

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
		@NotBlank(message = "{validation.not-blank}")
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
		@NotBlank(message = "{validation.not-blank}")
		private String imageName;
		@NotBlank(message = "{validation.not-blank}")
		private ImageType imageType;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Password {
		@NotBlank(message = "{validation.not-blank}")
		private String prevPassword;
		@NotBlank(message = "{validation.not-blank}")
		private String newPassword;

		public MemberServiceRequest.Password toServiceRequest(Long loginId) {
			return MemberServiceRequest.Password.builder()
				.prevPassword(prevPassword)
				.newPassword(newPassword)
				.loginId(loginId)
				.build();
		}
	}
}
