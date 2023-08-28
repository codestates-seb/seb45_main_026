package com.server.domain.member.service.dto.response;

import java.time.LocalDateTime;

import com.server.domain.member.entity.Grade;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {
	private Long memberId;
	private String email;
	private String nickname;
	private String imageUrl;
	private Grade grade;
	private int reward;
	private LocalDateTime createdDate;
}
