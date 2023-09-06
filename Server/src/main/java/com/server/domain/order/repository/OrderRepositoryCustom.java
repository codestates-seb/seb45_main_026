package com.server.domain.order.repository;

import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.video.entity.Video;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryCustom {

    Long deleteCartByMemberAndOrderId1(Long memberId, String orderId);

    Long deleteCartByMemberAndOrderId2(Long memberId, String orderId);

    Optional<Order> findByIdWithVideos(String orderId);

    List<Video> findWatchVideosAfterPurchaseById(String orderId);

    Boolean findWatchVideoAfterPurchaseByVideoId(String orderId, Long videoId);

    Optional<OrderVideo> findOrderVideoByVideoId(String orderId, Long videoId);
}
