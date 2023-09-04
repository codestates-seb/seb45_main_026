package com.server.domain.member.service.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.server.domain.cart.entity.Cart;
import com.server.domain.video.entity.Video;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class CartsResponse {
	private Long videoId;
	private String videoName;
	private String thumbnailUrl;
	private int views;
	private LocalDateTime createdDate;
	private int price;
	private Channel channel;

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	@Getter
	@Builder
	public static class Channel {
		private Long memberId;
		private String channelName;
		private int subscribes;
		private String imageUrl;

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
	}
}
