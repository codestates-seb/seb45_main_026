package com.server.domain.member.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.server.domain.member.controller.dto.PlaylistsSort;
import com.server.domain.video.controller.dto.request.VideoSort;

@Component
public class StringToPlaylistsSortConverter implements Converter<String, PlaylistsSort> {

	@Override
	public PlaylistsSort convert(String source) {
		return PlaylistsSort.fromUrl(source);
	}
}
