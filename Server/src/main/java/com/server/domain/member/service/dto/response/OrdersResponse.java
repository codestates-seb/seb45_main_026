package com.server.domain.member.service.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.weaver.ast.Or;

import com.server.domain.order.entity.OrderStatus;
import com.server.domain.order.entity.OrderVideo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrdersResponse {
	private String orderId;
	private int reward;
	private int orderCount;
	private OrderStatus orderStatus;
	private LocalDateTime createdDate;
	private List<OrderVideo> orderVideos;

	@Getter
	@Builder
	public static class OrderVideo {
		private Long videoId;
		private String videoName;
		private String thumbnailFile;
		private String channelName;
		private int price; //OrderVideo의 가격
	}
}
