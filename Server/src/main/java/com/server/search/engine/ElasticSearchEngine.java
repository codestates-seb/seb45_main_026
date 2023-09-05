package com.server.search.engine;

import java.util.List;

import org.springframework.stereotype.Service;

import com.server.domain.channel.entity.Channel;
import com.server.domain.video.entity.Video;
import com.server.search.engine.dto.VideoChannelSearchResponse;

@Service("elastic")
public class ElasticSearchEngine implements SearchEngine {
	@Override
	public List<Video> searchVideos(String keyword) {
		return null;
	}

	@Override
	public List<Channel> searchChannels(String keyword) {
		return null;
	}

	@Override
	public VideoChannelSearchResponse searchVideosAndChannels(String keyword) {
		return null;
	}
}
