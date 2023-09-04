package com.server.search.engine;

import java.util.List;

import org.springframework.stereotype.Service;

import com.server.domain.video.entity.Video;

@Service("mysql")
public class MySQLSearchEngine implements SearchEngine {
	public List<Video> searchVideos() {
		return null;
	};
}
