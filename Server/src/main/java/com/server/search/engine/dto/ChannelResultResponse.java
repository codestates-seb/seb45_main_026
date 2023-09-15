package com.server.search.engine.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ChannelResultResponse {
	private Long channelId;
	private String channelName;
	private String description;
	private Integer subscribers;
	private Boolean isSubscribed;
	private String imageUrl;
}
