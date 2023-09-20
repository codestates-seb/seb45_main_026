package com.server.domain.member.service.dto.response;

import java.time.LocalDateTime;

import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Grade;
import com.server.domain.member.entity.Member;

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
	private Authority authority;
	private LocalDateTime createdDate;

	public static ProfileResponse getMember(Member member, String imageUrl) {
		return ProfileResponse.builder()
			.memberId(member.getMemberId())
			.email(member.getEmail())
			.nickname(member.getNickname())
			.imageUrl(imageUrl)
			.grade(member.getGrade())
			.reward(member.getReward())
			.authority(member.getAuthority())
			.createdDate(member.getCreatedDate())
			.build();
	}
}
