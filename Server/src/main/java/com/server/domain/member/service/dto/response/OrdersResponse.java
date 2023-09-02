package com.server.domain.member.service.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.aspectj.weaver.ast.Or;

import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.order.entity.OrderVideo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrdersResponse {
	private String orderId; //order
	private int reward; //order
	private int orderCount; //orderVideos size
	private OrderStatus orderStatus; //order
	private LocalDateTime createdDate; //order
	private List<OrderVideo> orderVideos; //ordervideo

	@Getter
	@Builder
	public static class OrderVideo {
		private Long videoId; //video
		private String videoName; //video
		private String thumbnailFile; //video + channel.member.memberid
		private String channelName; //channel
		private int price; //video
	}

	public static List<OrdersResponse> convert(List<Order> orders) {
		return orders.stream()
			.map(order -> OrdersResponse.builder()
				.orderId(order.getOrderId())
				.reward(order.getReward())
				.orderCount(order.getOrderVideos().size())
				.orderStatus(order.getOrderStatus())
				.createdDate(order.getCreatedDate())
				.orderVideos(order.getOrderVideos().stream()
					.map(orderVideo -> OrdersResponse.OrderVideo.builder()
						.videoId(orderVideo.getVideo().getVideoId())
						.videoName(orderVideo.getVideo().getVideoName())
						.thumbnailFile(orderVideo.getVideo().getChannel().getMember().getMemberId() + "/" + orderVideo.getVideo().getThumbnailFile())
						.channelName(orderVideo.getVideo().getChannel().getChannelName())
						.price(orderVideo.getVideo().getPrice())
						.build())
					.collect(Collectors.toList()))
				.build())
			.collect(Collectors.toList());
	}
}
