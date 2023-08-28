package com.server.domain.member.service.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubscribesResponse {
	private Long memberId;
	private String channelName;
	private int subscribes;
	private String imageUrl;
}
