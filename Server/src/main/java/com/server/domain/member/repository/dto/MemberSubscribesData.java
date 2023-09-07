package com.server.domain.member.repository.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class MemberSubscribesData {
	private Long memberId;
	private String channelName;
	private int subscribes;
	private String imageUrl;


	public MemberSubscribesData(Long memberId, String channelName, int subscribes, String imageUrl) {
		this.memberId = memberId;
		this.channelName = channelName;
		this.subscribes = subscribes;
		this.imageUrl = imageUrl;
	}
}
