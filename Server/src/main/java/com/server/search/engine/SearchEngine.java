package com.server.search.engine;

import java.util.List;

import com.server.domain.channel.entity.Channel;
import com.server.domain.video.entity.Video;
import com.server.search.engine.dto.VideoChannelSearchResponse;

public interface SearchEngine {
	List<Video> searchVideos(String keyword);

	List<Channel> searchChannels(String keyword);

	VideoChannelSearchResponse searchVideosAndChannels(String keyword);
}
