package com.server.search.engine;

import java.util.List;

import com.server.domain.video.service.dto.request.VideoGetServiceRequest;
import com.server.domain.video.service.dto.response.VideoPageResponse;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.server.domain.channel.entity.Channel;
import com.server.domain.video.entity.Video;
import com.server.search.engine.dto.ChannelResultResponse;
import com.server.search.engine.dto.VideoChannelSearchResponse;
import com.server.search.repository.dto.ChannelSearchResult;
import com.server.search.repository.dto.VideoSearchResult;

@Service("elastic")
public class ElasticSearchEngine implements SearchEngine {

	@Override
	public VideoChannelSearchResponse searchVideosAndChannels(String keyword, int limit) {
		return null;
	}

	@Override
	public Page<ChannelResultResponse> searchChannelResults(String keyword, int page, int size, String sort,
		Long loginId) {
		return null;
	}
}
