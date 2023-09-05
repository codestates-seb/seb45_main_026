package com.server.domain.member.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistChannelResponse {
	private Long memberId; // 채널 소유자인 회원의 아이디
	private String channelName;
	private String profileImageUrl;
	private Long videoCount;
	private Boolean isSubscribed;
	private Integer subscribers;
}
