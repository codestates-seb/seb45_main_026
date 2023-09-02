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

	public static List<CartsResponse> convert(List<Cart> carts) {
		return carts.stream()
		    .map(cart -> CartsResponse.builder()
		        .videoId(cart.getVideo().getVideoId())
		        .videoName(cart.getVideo().getVideoName())
		        .thumbnailUrl(cart.getVideo().getThumbnailFile())
		        .views(cart.getVideo().getView())
		        .createdDate(cart.getCreatedDate())
		        .price(cart.getPrice())
		        .channel(CartsResponse.Channel.builder()
		            .memberId(cart.getVideo().getChannel().getMember().getMemberId())
		            .channelName(cart.getVideo().getChannel().getChannelName())
		            .subscribes(cart.getVideo().getChannel().getSubscribers())
		            .imageUrl(cart.getVideo().getChannel().getMember().getImageFile())
		            .build())
		        .build())
		    .collect(Collectors.toList());
	}
}
