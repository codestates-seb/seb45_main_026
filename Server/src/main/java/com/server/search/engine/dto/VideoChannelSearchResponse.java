package com.server.search.engine.dto;

import java.util.ArrayList;
import java.util.List;

import com.server.domain.channel.entity.Channel;
import com.server.domain.video.entity.Video;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VideoChannelSearchResponse {
	@Builder.Default
	private List<Video> videos = new ArrayList<>();
	@Builder.Default
	private List<Channel> channels = new ArrayList<>();
}
