package com.server.domain.member.service.dto.response;

import java.util.ArrayList;

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
	private String imageUrl;
	private Long videoCount;
	private Boolean isSubscribed;
	private Integer subscribers;
	@Builder.Default
	private ArrayList<Object> list = new ArrayList<>();
}
