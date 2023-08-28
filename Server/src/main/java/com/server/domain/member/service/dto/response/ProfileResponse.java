package com.server.domain.member.service.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {
	private Long memberId;
	private String email;
	private String nickname;
	private String imageUrl;
	private int reward;
	private LocalDateTime createdDate;
}
