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
public class SubscribesResponse {
	private Long memberId;
	private String channelName;
	private int subscribes;
	private String imageUrl;
}
