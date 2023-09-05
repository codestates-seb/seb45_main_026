package com.server.domain.member.service.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PlaylistChannelDetailsResponse {
	private Long videoId;
	private String videoName;
	private String description;
	private String thumbnailImageUrl;
	private int view;
	private Float star;
}
