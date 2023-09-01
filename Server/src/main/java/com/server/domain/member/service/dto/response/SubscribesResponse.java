package com.server.domain.member.service.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.server.domain.member.repository.dto.MemberSubscribesData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscribesResponse {
	private Long memberId;
	private String channelName;
	private int subscribes;
	private String imageUrl;

	public static List<SubscribesResponse> convertSubscribesResponse(List<MemberSubscribesData> memberSubscribesData) {

		return memberSubscribesData.stream()
			.map(data -> {
				SubscribesResponse response = new SubscribesResponse();
				response.setMemberId(data.getMemberId());
				response.setChannelName(data.getChannelName());
				response.setSubscribes(data.getSubscribes());
				response.setImageUrl(data.getImageUrl());
				return response;
			})
			.collect(Collectors.toList());
	}
}
