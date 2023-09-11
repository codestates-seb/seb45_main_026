package com.server.domain.member.repository;

import static com.server.domain.cart.entity.QCart.*;
import static com.server.domain.channel.entity.QChannel.*;
import static com.server.domain.member.entity.QMember.*;
import static com.server.domain.order.entity.QOrder.*;
import static com.server.domain.order.entity.QOrderVideo.*;
import static com.server.domain.subscribe.entity.QSubscribe.*;
import static com.server.domain.video.entity.QVideo.*;
import static com.server.domain.watch.entity.QWatch.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.cart.entity.Cart;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.member.entity.QMember;
import com.server.domain.member.repository.dto.MemberVideoData;
import com.server.domain.member.repository.dto.QMemberVideoData;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.video.entity.QVideo;
import com.server.domain.video.entity.Video;
import com.server.domain.watch.entity.Watch;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    //order 의 상태가 COMPLETE 인지 확인합니다.
    @Override
    public Boolean checkMemberPurchaseVideo(Long memberId, Long videoId) {
        JPAQuery<Order> query = queryFactory
                .selectFrom(order)
                .join(order.member, member)
                .join(order.orderVideos, orderVideo)
                .join(orderVideo.video, video)
                .where(member.memberId.eq(memberId)
                        .and(order.orderStatus.eq(OrderStatus.COMPLETED))
                        .and(video.videoId.eq(videoId)));

        Order result = query.fetchOne();

        return result != null;
    }

    public List<Boolean> checkMemberPurchaseVideos(Long memberId, List<Long> videoIds) {

        List<Long> memberPurchases = queryFactory
                .select(video.videoId)
                .from(video)
                .join(video.orderVideos, orderVideo)
                .join(orderVideo.order, order)
                .join(order.member, member)
                .where(member.memberId.eq(memberId).and(order.orderStatus.eq(OrderStatus.COMPLETED)))
                .fetch();

        return videoIds.stream()
                .map(memberPurchases::contains)
                .collect(Collectors.toList());
    }

    @Override
    public List<Boolean> checkMemberSubscribeChannel(Long memberId, List<Long> ownerMemberIds) {

        QMember channelOwner = new QMember("channelOwner");
        QMember loginMember = new QMember("loginMember");

        List<Long> memberSubcribeList = queryFactory
                .select(channelOwner.memberId)
                .from(channelOwner)
                .join(channelOwner.channel, channel)
                .join(channel.subscribes, subscribe1)
                .join(subscribe1.member, loginMember)
                .where(loginMember.memberId.eq(memberId))
                .fetch();

        return ownerMemberIds.stream()
                .map(memberSubcribeList::contains)
                .collect(Collectors.toList());
    }

            // 주문했다가 취소한 경우도 포함합니다.
    @Override
    public List<MemberVideoData> getMemberPurchaseVideo(Long memberId) {

        return queryFactory
                .select(new QMemberVideoData(
                        video.videoId,
                        order.orderId,
                        video.videoName,
                        video.description,
                        video.thumbnailFile,
                        video.videoFile,
                        video.view,
                        video.star,
                        video.price,
                        order.orderStatus,
                        order.modifiedDate,
                        channel.channelId,
                        channel.channelName
                ))
                .from(video)
                .join(video.orderVideos, orderVideo)
                .join(orderVideo.order, order)
                .join(order.member, member)
                .leftJoin(video.channel, channel)
                .where(member.memberId.eq(memberId))
                .fetch();
    }

    @Override
    public Optional<Member> findByIdWithChannel(Long memberId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(member)
                        .join(member.channel, channel).fetchJoin()
                        .where(member.memberId.eq(memberId))
                        .fetchOne()
        );
    }

    @Override
    public Page<Channel> findSubscribeWithChannelForMember(Long memberId, Pageable pageable) {

        JPAQuery<Channel> query = queryFactory
            .select(channel)
            .from(subscribe1)
            .join(subscribe1.channel, channel)
            .join(subscribe1.member, member)
            .where(member.memberId.eq(memberId))
            .orderBy(subscribe1.createdDate.desc());

        long totalCount = query.fetchCount();

        List<Channel> results = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public Page<Cart> findCartsOrderByCreatedDateForMember(Long memberId, Pageable pageable) {

        JPAQuery<Cart> query = queryFactory
            .selectFrom(cart)
            .leftJoin(cart.video, video).fetchJoin()
            .leftJoin(video.channel, channel).fetchJoin()
            .leftJoin(channel.member, member).fetchJoin()
            .where(cart.member.memberId.eq(memberId))
            .orderBy(cart.createdDate.desc());

        long totalCount = query.fetchCount();

        List<Cart> results = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public Page<Order> findOrdersOrderByCreatedDateForMember(Long memberId, Pageable pageable, int month) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = currentDateTime.minusMonths(month).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endDateTime = currentDateTime.withHour(23).withMinute(59).withSecond(59);

        JPAQuery<Order> query = queryFactory
            .selectFrom(order)
            .leftJoin(order.orderVideos, orderVideo).fetchJoin()
            .leftJoin(orderVideo.video, video).fetchJoin()
            .leftJoin(video.channel, channel).fetchJoin()
            .leftJoin(channel.member, member).fetchJoin()
            .where(
                order.member.memberId.eq(memberId)
                .and(order.createdDate.between(startDateTime, endDateTime))
            )
            .orderBy(order.createdDate.desc())
            .distinct();

        long totalCount = query.fetchCount();

        List<Order> results = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public Page<Video> findPlaylistsOrderBySort(Long memberId, Pageable pageable, String sort) {
        OrderSpecifier<?> orderSpecifier;

        QVideo video = new QVideo("playlist");

        switch (sort) {
            case "name":
                orderSpecifier = video.videoName.asc();
                break;
            case "star":
                orderSpecifier = video.star.desc();
                break;
            case "createdDate":
            default:
                orderSpecifier = video.createdDate.desc();
                break;
        }

        JPAQuery<Video> query =  queryFactory
            .selectFrom(video)
            .join(video.orderVideos, orderVideo)
            .join(orderVideo.order, order)
            .join(video.channel, channel).fetchJoin()
            .join(channel.member, member).fetchJoin()
            .where(
                order.member.memberId.eq(memberId)
                .and(order.orderStatus.eq(OrderStatus.COMPLETED))
            )
            .orderBy(orderSpecifier);

        long totalCount = query.fetchCount();

        List<Video> results = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public Page<Watch> findWatchesForMember(Long memberId, Pageable pageable, int days) {
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = endDateTime.minusDays(days);

        JPAQuery<Watch> query = queryFactory
            .selectFrom(watch)
            .leftJoin(watch.video, video).fetchJoin()
            .leftJoin(video.channel, channel).fetchJoin()
            .where(
                watch.member.memberId.eq(memberId)
                    .and(watch.modifiedDate.between(startDateTime, endDateTime))
            )
            .orderBy(watch.modifiedDate.desc());

        long totalCount = query.fetchCount();

        List<Watch> results = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public Page<Tuple> findPlaylistGroupByChannelName(Long memberId, Pageable pageable) {

        JPAQuery<Tuple> query = queryFactory
            .select(
                channel.member.memberId,
                channel.channelName,
                channel.member.imageFile,
                video.videoId.count(),
                JPAExpressions
                    .select(subscribe1.subscribe)
                    .from(subscribe1)
                    .where(
                        subscribe1.channel.channelId.eq(channel.channelId)
                            .and(subscribe1.member.memberId.eq(memberId))
                    )
                    .exists().as("isSubscribed"),
                channel.subscribers
            )
            .from(member)
            .join(member.orders, order)
            .join(order.orderVideos, orderVideo)
            .join(orderVideo.video, video)
            .join(video.channel, channel)
            .where(
                order.member.memberId.eq(memberId)
                .and(order.orderStatus.eq(OrderStatus.COMPLETED))
            )
            .groupBy(channel.member.memberId)
            .orderBy(channel.channelName.asc());

        long totalCount = query.fetchCount();

        List<Tuple> tuples = query
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        return new PageImpl<>(tuples, pageable, totalCount);
    }

    public Page<Video> findPlaylistChannelDetails(Long loginId, Long memberId) {

        JPAQuery<Video> query = queryFactory
            .select(video)
            .from(order)
            .join(order.orderVideos, orderVideo)
            .join(orderVideo.video, video)
            .join(video.channel, channel)
            .where(
                order.member.memberId.eq(loginId)
                .and(order.orderStatus.eq(OrderStatus.COMPLETED))
                .and(video.channel.member.memberId.eq(memberId))
            )
            .orderBy(video.videoName.asc());

        List<Video> videos = query
            .fetch();

        return new PageImpl<>(videos);
    }
}
