package com.server.domain.order.repository;

public interface OrderRepositoryCustom {

    Long deleteCartByMemberAndOrderId1(Long memberId, String orderId);

    Long deleteCartByMemberAndOrderId2(Long memberId, String orderId);
}
