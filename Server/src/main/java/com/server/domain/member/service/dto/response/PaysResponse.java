package com.server.domain.member.service.dto.response;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.weaver.ast.Or;

import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.order.entity.OrderVideo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaysResponse {
	private String orderId;
	private int reward;
	private int orderCount;
	private OrderStatus orderStatus;

	// private OrderVideos orderVideos;

	// @Getter
	// @Builder
	// public static class OrderVideos {
	// 	@Builder.Default
	// 	private List<OrderVideo> orderVideos = new ArrayList<>();
	// }
}
