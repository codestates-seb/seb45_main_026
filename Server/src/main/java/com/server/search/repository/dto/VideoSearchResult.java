package com.server.search.repository.dto;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VideoSearchResult {
	private Long videoId;
	private String thumbnailFile;
	private String videoName;
	private Long memberId;

	public static List<VideoSearchResult> converter(List<Tuple> tuples) {

			 return tuples.stream()
				.map(tuple -> new VideoSearchResult(
					tuple.get("video_id", BigInteger.class).longValue(),
					tuple.get("thumbnail_file", String.class),
					tuple.get("video_name", String.class),
					tuple.get("member_id", BigInteger.class).longValue()
				))
				.collect(Collectors.toList());
	}
}
