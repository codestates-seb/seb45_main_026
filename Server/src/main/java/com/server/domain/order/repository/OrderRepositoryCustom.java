package com.server.domain.order.repository;

import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.order.repository.dto.AdjustmentData;
import com.server.domain.video.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryCustom {

    Long deleteCartByMemberAndOrderId(Long memberId, String orderId);

    List<OrderVideo> findOrderedVideosByMemberId(Long memberId, List<Long> videoIds);

    Optional<Order> findByIdWithVideos(Long memberId, String orderId);

    List<Video> findWatchVideosAfterPurchaseById(Order order);

    Boolean checkIfWatchAfterPurchase(Order order, Long videoId);

    Optional<OrderVideo> findOrderVideoByVideoId(String orderId, Long videoId);

    Page<AdjustmentData> findByPeriod(Long memberId, Pageable pageable, Integer month, Integer year, String sort);

    Integer calculateAmount(Long memberId, Integer month, Integer year);
}
