package com.server.search.engine.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ChannelResultResponse {
	private Long memberId;
	private String channelName;
	private String description;
	private Integer subscribes;
	private Boolean isSubscribed;
	private String imageUrl;
}
