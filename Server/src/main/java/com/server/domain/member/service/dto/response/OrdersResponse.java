package com.server.domain.member.service.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.server.domain.order.entity.OrderStatus;
import com.server.domain.order.entity.OrderVideo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrdersResponse {
	private String orderId; //order
	private int amount; //order
	private int orderCount; //orderVideos size
	private OrderStatus orderStatus; //order
	private LocalDateTime createdDate; //order
	private LocalDateTime completedDate;
	private List<OrderVideo> orderVideos; //ordervideo

	@Getter
	@Builder
	public static class OrderVideo {
		private Long videoId; //video
		private String videoName; //video
		private String thumbnailFile; //video + channel.member.memberid
		private String channelName; //channel
		private int price; //video
		private OrderStatus orderStatus;
	}
}
