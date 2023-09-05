package com.server.search.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import com.server.domain.video.entity.Video;
import com.server.search.engine.SearchEngine;

@RestController
@RequestMapping// 주소는 나중에 정하기
public class SearchController {
	private final SearchEngine searchEngine;

	public SearchController(@Qualifier("mysql") SearchEngine searchEngine) {
		this.searchEngine = searchEngine;
	}

	@GetMapping("/search.action")
	public ResponseEntity<String> wordSearchShow(@RequestParam("keyword") String keyword) {

		List<Video> wordList = searchEngine.searchVideos(keyword);

		if (wordList != null) {

			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>("No results found", HttpStatus.NOT_FOUND);
		}
	}
}
