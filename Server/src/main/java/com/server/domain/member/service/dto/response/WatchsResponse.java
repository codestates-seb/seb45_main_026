package com.server.domain.member.service.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WatchsResponse {
	private Long videoId;
	private String videoName;
	private String thumbnailFile;
	private LocalDateTime modifiedDate;
	private Channel channel;

	@Getter
	@Builder
	public static class Channel {
		private Long memberId;
		private String channelName;
		private String profileImageUrl;
	}
}
