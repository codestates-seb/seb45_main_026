package com.server.domain.order.repository;

import com.server.domain.order.entity.Order;

import java.util.Optional;

public interface OrderRepositoryCustom {

    Long deleteCartByMemberAndOrderId1(Long memberId, String orderId);

    Long deleteCartByMemberAndOrderId2(Long memberId, String orderId);

    Optional<Order> findByIdWithVideos(String orderId);
}
