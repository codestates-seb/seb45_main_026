package com.server.domain.member.controller.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.server.domain.member.service.dto.request.MemberServiceRequest;
import com.server.global.validation.ImageTypeValid;
import com.server.module.s3.service.dto.ImageType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberApiRequest {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Nickname {
		@NotNull(message = "{validation.auth.nickname}")
		@Size(min = 1, max = 20, message = "{validation.size}")
		@Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,20}$", message = "{validation.auth.nickname}")
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
		@ImageTypeValid(message = "{validation.imageType}")
		private ImageType imageType;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Password {
		@NotNull(message = "{validation.auth.password}")
		@Size(min = 9, max = 20, message = "{validation.size}")
		@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$", message = "{validation.auth.password}")
		private String prevPassword;

		@NotNull(message = "{validation.auth.password}")
		@Size(min = 9, max = 20, message = "{validation.size}")
		@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$", message = "{validation.auth.password}")
		private String newPassword;

		public MemberServiceRequest.Password toServiceRequest() {
			return MemberServiceRequest.Password.builder()
				.prevPassword(prevPassword)
				.newPassword(newPassword)
				.build();
		}
	}
}
