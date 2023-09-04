package com.server.search.engine;

import java.util.List;

import com.server.domain.video.entity.Video;

public interface SearchEngine {
	List<Video> searchVideos();
}
