package com.server.domain.member.controller.dto;

import org.springframework.beans.TypeMismatchException;

import com.server.domain.video.controller.dto.request.VideoSort;
import com.server.global.entity.BaseEnum;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PlaylistsSort implements BaseEnum {
	CREATED_DATE("최신순", "created-date", "createdDate"),
	NAME("이름 순", "name", "name"),
	STAR("별점 순", "star", "star");

	private final String description;
	private final String url;
	private final String sort;

	@Override
	public String getName() {
		return this.url;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	public String getSort() {
		return this.sort;
	}

	public static PlaylistsSort fromUrl(String url) {
		for (PlaylistsSort playlistsSort : values()) {
			if (playlistsSort.url.equals(url)) {
				return playlistsSort;
			}
		}
		throw new TypeMismatchException(url, VideoSort.class);
	}
}