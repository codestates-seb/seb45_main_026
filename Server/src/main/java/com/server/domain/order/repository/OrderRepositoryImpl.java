package com.server.domain.order.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.order.entity.QOrderVideo;
import com.server.domain.video.entity.QVideo;
import com.server.domain.video.entity.Video;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static com.server.domain.cart.entity.QCart.cart;
import static com.server.domain.member.entity.QMember.*;
import static com.server.domain.order.entity.QOrder.*;
import static com.server.domain.order.entity.QOrderVideo.orderVideo;
import static com.server.domain.video.entity.QVideo.video;
import static com.server.domain.watch.entity.QWatch.watch;

public class OrderRepositoryImpl implements OrderRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public OrderRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Long deleteCartByMemberAndOrderId(Long memberId, String orderId) {

        return queryFactory.delete(cart)
                .where(cart.video.orderVideos.any().order.orderId.eq(orderId)
                        .and(cart.member.memberId.eq(memberId))
                ).execute();
    }

    @Override
    public List<Video> findPurchasedVideosByMemberId(Long memberId) {
        return queryFactory.selectFrom(video)
                .join(video.orderVideos, orderVideo)
                .join(orderVideo.order, order)
                .where(order.member.memberId.eq(memberId)
                        .and(orderVideo.orderStatus.in(OrderStatus.COMPLETED, OrderStatus.ORDERED))
                ).fetch();
    }

    @Override
    public Optional<Order> findByIdWithVideos(Long memberId, String orderId) {

        return Optional.ofNullable(
                queryFactory.selectFrom(order)
                        .join(order.member, member).fetchJoin()
                        .join(order.orderVideos, orderVideo).fetchJoin()
                        .join(orderVideo.video, video).fetchJoin()
                        .where(order.orderId.eq(orderId)).fetchOne()
        );
    }

    public List<Video> findWatchVideosAfterPurchaseById(Order checkOrder) {

        List<Long> orderVideoIds = queryFactory.select(video.videoId)
                .from(order)
                .join(order.orderVideos, orderVideo)
                .join(orderVideo.video, video)
                .where(order.orderId.eq(checkOrder.getOrderId())
                ).fetch();

        return queryFactory
                .selectFrom(video)
                .join(video.watches, watch)
                .join(watch.member, member)
                .where(video.videoId.in(orderVideoIds),
                        watch.modifiedDate.after(checkOrder.getCompletedDate())
                ).fetch();
    }

    public Boolean checkIfWatchAfterPurchase(Order checkOrder, Long videoId) {

        Video watchVideo = queryFactory
                .selectFrom(video)
                .join(video.watches, watch)
                .join(watch.member, member)
                .where(video.videoId.eq(videoId),
                        watch.modifiedDate.after(checkOrder.getCompletedDate())
                ).fetchOne();

        return watchVideo != null;
    }

    public Optional<OrderVideo> findOrderVideoByVideoId(String orderId, Long videoId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(orderVideo)
                        .join(orderVideo.order, order).fetchJoin()
                        .join(order.member, member).fetchJoin()
                        .join(orderVideo.video, video).fetchJoin()
                        .where(order.orderId.eq(orderId)
                                .and(video.videoId.eq(videoId))
                        ).fetchOne()
        );
    }
}
