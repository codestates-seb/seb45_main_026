package com.server.domain.member.service.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartsResponse {
	/*
	{
      "videoId":1,
      "videoName":"vlog first day",
      "thumbnailUrl":"https://s3_thumbnailUrl1",
      "views":5000,
      "price":20000
	 */

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
