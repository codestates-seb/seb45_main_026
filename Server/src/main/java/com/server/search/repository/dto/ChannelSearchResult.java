package com.server.search.repository.dto;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import com.server.domain.member.entity.Member;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChannelSearchResult {
	private Long memberId;
	private String channelName;
	private String imageFile;

	public static List<ChannelSearchResult> converter(List<Tuple> tuples) {

		return tuples.stream()
			.map(tuple -> ChannelSearchResult.builder()
				.memberId(tuple.get("member_id", BigInteger.class).longValue())
				.channelName(tuple.get("channel_name", String.class))
				.imageFile(tuple.get("image_file", String.class))
				.build()
			)
			.collect(Collectors.toList());
	}
}
