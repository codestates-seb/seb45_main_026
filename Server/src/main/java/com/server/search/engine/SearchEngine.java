package com.server.search.engine;

import com.server.search.engine.dto.ChannelResultResponse;
import com.server.search.engine.dto.VideoChannelSearchResponse;
import org.springframework.data.domain.Page;

public interface SearchEngine {
	VideoChannelSearchResponse searchVideosAndChannels(String keyword, int limit);

	Page<ChannelResultResponse> searchChannelResults(String keyword, int page, int size, String sort, Long loginId);
}
