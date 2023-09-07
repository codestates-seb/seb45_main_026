package com.server.domain.member.service.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PlaylistChannelDetailsResponse {
	private Long videoId;
	private String videoName;
	private String description;
	private String thumbnailUrl;
	private int view;
	private Float star;
	private LocalDateTime createdDate;
}
