package com.server.domain.order.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.adjustment.repository.dto.AdjustmentData;
import com.server.domain.video.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.server.domain.cart.entity.QCart.cart;
import static com.server.domain.member.entity.QMember.*;
import static com.server.domain.order.entity.QOrder.*;
import static com.server.domain.order.entity.QOrderVideo.orderVideo;
import static com.server.domain.video.entity.QVideo.video;
import static com.server.domain.watch.entity.QWatch.watch;

public class OrderRepositoryImpl implements OrderRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public OrderRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.em = em;
    }

    @Override
    public Long deleteCartByMemberAndOrderId(Long memberId, String orderId) {

        return queryFactory.delete(cart)
                .where(cart.video.orderVideos.any().order.orderId.eq(orderId)
                        .and(cart.member.memberId.eq(memberId))
                ).execute();
    }

    @Override
    public List<OrderVideo> findOrderedVideosByMemberId(Long memberId, List<Long> videoIds) {
        return queryFactory.selectFrom(orderVideo)
                .join(orderVideo.order, order).fetchJoin()
                .where(order.member.memberId.eq(memberId),
                        orderVideo.orderStatus.in(OrderStatus.COMPLETED, OrderStatus.ORDERED),
                        orderVideo.video.videoId.in(videoIds)
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
                        watch.member.memberId.eq(checkOrder.getMember().getMemberId()),
                        watch.modifiedDate.after(checkOrder.getCompletedDate())
                ).fetch();
    }

    public Boolean checkIfWatchAfterPurchase(Order checkOrder, Long videoId) {

        Video watchVideo = queryFactory
                .selectFrom(video)
                .join(video.watches, watch)
                .join(watch.member, member)
                .where(video.videoId.eq(videoId),
                        watch.member.memberId.eq(checkOrder.getMember().getMemberId()),
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

    @Override
    public Page<AdjustmentData> findByPeriod(Long memberId, Pageable pageable, Integer month, Integer year, String sort) {

        TypedQuery<Object[]> jpqlQuery = em.createQuery(
                        "SELECT v.videoId, " +
                                "v.videoName, " +
                                "SUM(ov.price) AS totalSaleAmount, " +
                                "SUM(CASE WHEN ov.orderStatus = 'CANCELED' THEN ov.price ELSE 0 END) AS refundAmount " +
                                "FROM Video v " +
                                "LEFT JOIN v.orderVideos ov " +
                                "LEFT JOIN ov.order o " +
                                "WHERE o.paymentKey != null " +
                                "AND v.channel.id = :memberId " +
                                 getDateCondition(month, year) +
                                "GROUP BY v.videoId " +
                                getAdjustmentSort(sort), Object[].class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        setDateCondition(month, year, jpqlQuery);

        List<Object[]> resultList = jpqlQuery.getResultList();

        List<AdjustmentData> videoReportDatas = resultList.stream()
                .map(arr -> new AdjustmentData(
                        (Long) arr[0],
                        (String) arr[1],
                        ((Number) arr[2]).intValue(),
                        ((Number) arr[3]).intValue()
                ))
                .collect(Collectors.toList());

        JPAQuery<Long> countQuery = queryFactory.select(video.count())
                .from(video)
                .join(video.orderVideos, orderVideo)
                .where(video.channel.channelId.eq(memberId));

        return new PageImpl<>(videoReportDatas, pageable, countQuery.fetchOne());
    }

    @Override
    public Integer calculateAmount(Long memberId, Integer month, Integer year) {

        TypedQuery<Long> jpqlCountQuery = em.createQuery(
                                "SELECT SUM(ov.price) - SUM(CASE WHEN ov.orderStatus = 'CANCELED' THEN ov.price ELSE 0 END) AS total " +
                                "FROM Video v " +
                                "JOIN v.orderVideos ov " +
                                "JOIN ov.order o " +
                                "WHERE o.paymentKey != null " +
                                "AND v.channel.id = :memberId " +
                                getDateCondition(month, year), Long.class)
                .setParameter("memberId", memberId);

        setDateCondition(month, year, jpqlCountQuery);

        Long singleResult = jpqlCountQuery.getSingleResult();

        return singleResult == null ? 0 : singleResult.intValue();
    }

    private void setDateCondition(Integer month, Integer year, TypedQuery jpqlQuery) {

            if (year != null) {
                jpqlQuery.setParameter("year", year);
            }

            if (month != null) {
                jpqlQuery.setParameter("month", month);
            }
    }

    private String getDateCondition(Integer month, Integer year) {

        String dateCondition = "";

        if (year != null) {
            dateCondition += "AND FUNCTION('YEAR', o.completedDate) = :year ";
        }

        if (month != null) {
            dateCondition += "AND FUNCTION('MONTH', o.completedDate) = :month ";
        }

        return dateCondition;
    }



    private String getAdjustmentSort(String sort) {

        String order = "ORDER BY ";

        if (sort.equals("totalSaleAmount")) {
            order += "totalSaleAmount DESC, ";
        } else if (sort.equals("refundAmount")) {
            order += "refundAmount DESC, ";
        }

        return order + "v.createdDate DESC";
    }
}
