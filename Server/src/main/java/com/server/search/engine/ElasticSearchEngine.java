package com.server.search.engine;

import java.util.List;

import org.springframework.stereotype.Service;

import com.server.domain.channel.entity.Channel;
import com.server.domain.video.entity.Video;
import com.server.search.engine.dto.VideoChannelSearchResponse;
import com.server.search.repository.dto.ChannelSearchResult;
import com.server.search.repository.dto.VideoSearchResult;

@Service("elastic")
public class ElasticSearchEngine implements SearchEngine {

	@Override
	public VideoChannelSearchResponse searchVideosAndChannels(String keyword, int limit) {
		return null;
	}
}
