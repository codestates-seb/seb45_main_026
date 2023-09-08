package com.server.search.engine.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChannelSearchResponse {
	private Long memberId;
	private String channelName;
	private String imageUrl;
}
