package com.server.search.engine.dto;

import java.util.ArrayList;
import java.util.List;

import com.server.domain.channel.entity.Channel;
import com.server.domain.video.entity.Video;
import com.server.search.repository.dto.VideoSearchResult;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VideoChannelSearchResponse {
	private List<VideoSearchResponse> videos;
	private List<ChannelSearchResponse> channels;
}
