package com.server.search.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.domain.channel.entity.Channel;
import com.server.domain.video.entity.Video;
import com.server.search.engine.SearchEngine;
import com.server.search.engine.dto.VideoChannelSearchResponse;

@RestController
@RequestMapping("/search")
public class SearchController {
	private final SearchEngine searchEngine;

	public SearchController(@Qualifier("mysql") SearchEngine searchEngine) {
		this.searchEngine = searchEngine;
	}

	@GetMapping
	public ResponseEntity<String> wordSearchShow(@RequestParam("keyword") String keyword) {

		List<Video> videoList = searchEngine.searchVideos(keyword);
		List<Channel> channelList = searchEngine.searchChannels(keyword);
		VideoChannelSearchResponse responses = searchEngine.searchVideosAndChannels(keyword);

		if (true) {

			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>("No results found", HttpStatus.NOT_FOUND);
		}
	}
}
