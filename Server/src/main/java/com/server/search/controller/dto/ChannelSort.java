package com.server.search.controller.dto;

import org.springframework.beans.TypeMismatchException;

import com.server.domain.video.controller.dto.request.VideoSort;
import com.server.global.entity.BaseEnum;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ChannelSort implements BaseEnum {
	NAME("채널명 순", "name", "name"),
	SUBSCRIBERS("구독자 순", "subscribers", "subscribers"),
	DEFAULT("키워드 일치율 순", "default", "default"),

	;

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

	public static ChannelSort fromUrl(String url) {
		for (ChannelSort channelSort : values()) {
			if (channelSort.url.equals(url)) {
				return channelSort;
			}
		}
		throw new TypeMismatchException(url, ChannelSort.class);
	}
}
