package com.server.search.controller;

import com.server.domain.video.controller.dto.request.VideoSort;
import com.server.domain.video.service.VideoService;
import com.server.domain.video.service.dto.request.VideoGetServiceRequest;
import com.server.domain.video.service.dto.response.VideoPageResponse;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiPageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.global.reponse.ApiSingleResponse;
import com.server.search.engine.SearchEngine;
import com.server.search.engine.dto.VideoChannelSearchResponse;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/search")
@Validated
public class SearchController {

	private final SearchEngine searchEngine;
	private final VideoService videoService;

	@Autowired
	public SearchController(@Qualifier("mysql") SearchEngine searchEngine, VideoService videoService) {
		this.searchEngine = searchEngine;
		this.videoService = videoService;
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

	@GetMapping("/videos")
	public ResponseEntity<ApiPageResponse<VideoPageResponse>> videoSearch(
			@RequestParam("keyword") String keyword,
			@RequestParam(value = "page", defaultValue = "1") @Positive(message = "{validation.positive}") int page,
			@RequestParam(value = "size", defaultValue = "16") @Positive(message = "{validation.positive}") int size,
			@RequestParam(value = "sort", required = false) VideoSort sort,
			@RequestParam(value = "category", required = false) String category,
			@RequestParam(value = "subscribe", defaultValue = "false") boolean subscribe,
			@RequestParam(value = "free", required = false) Boolean free,
			@RequestParam(value = "is-purchased", defaultValue = "true") boolean isPurchased,
			@LoginId Long loginMemberId) {

		String sortValue = sort == null ? null : sort.getSort();

		VideoGetServiceRequest request = VideoGetServiceRequest.builder()
				.loginMemberId(loginMemberId)
				.page(page - 1)
				.size(size)
				.categoryName(category)
				.sort(sortValue)
				.subscribe(subscribe)
				.free(free)
				.isPurchased(isPurchased)
				.build();

		Page<VideoPageResponse> videos = videoService.searchVideos(keyword, request);

		return ResponseEntity.ok(ApiPageResponse.ok(videos, "비디오 목록 검색 성공"));
	}
}
