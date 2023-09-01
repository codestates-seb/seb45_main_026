package com.server.domain.member.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class SubscribesResponse {
	private Long memberId;
	private String channelName;
	private int subscribes;
	private String imageUrl;


	public SubscribesResponse(Long memberId, String channelName, int subscribes, String imageUrl) {
		this.memberId = memberId;
		this.channelName = channelName;
		this.subscribes = subscribes;
		this.imageUrl = imageUrl;
	}
}
