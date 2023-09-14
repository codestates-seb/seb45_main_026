package com.server.search.engine;

import java.util.List;

import com.server.domain.channel.entity.Channel;
import com.server.domain.video.entity.Video;
import com.server.domain.video.service.dto.request.VideoGetServiceRequest;
import com.server.domain.video.service.dto.response.VideoPageResponse;
import com.server.search.engine.dto.VideoChannelSearchResponse;
import com.server.search.repository.dto.ChannelSearchResult;
import com.server.search.repository.dto.VideoSearchResult;

public interface SearchEngine {
	VideoChannelSearchResponse searchVideosAndChannels(String keyword, int limit);
}
