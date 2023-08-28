package com.server.domain.member.service.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartsResponse {
	private Long videoId;
	private String videoName;
	private String thumbnailUrl;
	private int views;
	private LocalDateTime createdDate;
	private int price;
	private Channel channel;

	@Getter
	@Builder
	public static class Channel {
		private Long memberId;
		private String channelName;
		private int subscribes;
		private String imageUrl;
	}
}
