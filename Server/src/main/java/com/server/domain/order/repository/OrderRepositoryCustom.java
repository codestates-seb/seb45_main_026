package com.server.domain.order.repository;

import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.video.entity.Video;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryCustom {

    Long deleteCartByMemberAndOrderId(Long memberId, String orderId);

    List<Video> findPurchasedVideosByMemberId(Long memberId);

    Optional<Order> findByIdWithVideos(Long memberId, String orderId);

    List<Video> findWatchVideosAfterPurchaseById(Order order);

    Boolean checkIfWatchAfterPurchase(Order order, Long videoId);

    Optional<OrderVideo> findOrderVideoByVideoId(String orderId, Long videoId);
}
