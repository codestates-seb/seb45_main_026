package com.server.search.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.server.domain.channel.entity.Channel;
import com.server.domain.video.entity.Video;
import com.server.global.reponse.ApiSingleResponse;
import com.server.search.engine.SearchEngine;
import com.server.search.engine.dto.VideoChannelSearchResponse;

@RestController
@RequestMapping("/search")
public class SearchController {

	private final SearchEngine searchEngine;

	@Autowired
	public SearchController(@Qualifier("mysql") SearchEngine searchEngine) {
		this.searchEngine = searchEngine;
	}

	@GetMapping
	public ResponseEntity<ApiSingleResponse<VideoChannelSearchResponse>> search(
		@RequestParam("keyword") String keyword,
		@RequestParam(value = "limit", defaultValue = "3") int limit)
	{

		VideoChannelSearchResponse responses =
			searchEngine.searchVideosAndChannels("\"" + keyword + "\"", limit);

		return ResponseEntity.ok(ApiSingleResponse.ok(responses));
	}
}
