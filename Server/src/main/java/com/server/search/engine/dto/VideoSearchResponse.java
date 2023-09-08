package com.server.search.engine.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VideoSearchResponse {
	private Long videoId;
	private String videoName;
	private String thumbnailUrl;
}
