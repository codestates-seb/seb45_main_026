package com.server.search.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.search.engine.SearchEngine;

@RestController
@RequestMapping// 주소는 나중에 정하기
public class SearchController {
	private final SearchEngine searchEngine;

	public SearchController(@Qualifier("mysql") SearchEngine searchEngine) {
		this.searchEngine = searchEngine;
	}
}
