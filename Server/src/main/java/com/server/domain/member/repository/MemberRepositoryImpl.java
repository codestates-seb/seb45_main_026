package com.server.domain.member.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.cart.entity.Cart;
import com.server.domain.channel.entity.QChannel;
import com.server.domain.member.entity.Member;
import com.server.domain.member.entity.QMember;
import com.server.domain.member.repository.dto.MemberSubscribesData;
import com.server.domain.member.repository.dto.MemberVideoData;
import com.server.domain.member.repository.dto.QMemberVideoData;
import com.server.domain.member.service.dto.response.CartsResponse;
import com.server.domain.member.service.dto.response.OrdersResponse;
import com.server.domain.member.service.dto.response.PlaylistsResponse;
import com.server.domain.member.service.dto.response.RewardsResponse;
import com.server.domain.member.service.dto.response.WatchsResponse;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.reward.entity.Reward;
import com.server.domain.subscribe.entity.QSubscribe;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.video.entity.QVideo;
import com.server.domain.video.entity.Video;
import com.server.domain.watch.entity.Watch;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.server.domain.cart.entity.QCart.*;
import static com.server.domain.channel.entity.QChannel.channel;
import static com.server.domain.member.entity.QMember.*;
import static com.server.domain.order.entity.QOrder.*;
import static com.server.domain.order.entity.QOrderVideo.*;
import static com.server.domain.question.entity.QQuestion.*;
import static com.server.domain.reward.entity.QReward.*;
import static com.server.domain.subscribe.entity.QSubscribe.*;
import static com.server.domain.video.entity.QVideo.*;
import static com.server.domain.watch.entity.QWatch.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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

        List<Long> memberSubcribeList = queryFactory
                .select(subscribe1.channel.member.memberId)
                .from(subscribe1)
                .where(subscribe1.member.memberId.eq(memberId))
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
    public List<MemberSubscribesData> findSubscribeWithChannelForMember(Long memberId) {
        QMember loginMember = new QMember("loginMember");
        QChannel qChannel = QChannel.channel;
        QSubscribe qSubscribe = subscribe1;

        JPAQuery<MemberSubscribesData> query = queryFactory
            .select(Projections.constructor(
                MemberSubscribesData.class,
                qChannel.member.memberId,
                qChannel.channelName,
                qChannel.subscribers,
                qChannel.member.imageFile))
            .from(qSubscribe)
            .join(qSubscribe.channel, qChannel)
            .join(qSubscribe.member, loginMember)
            .where(loginMember.memberId.eq(memberId))
            .orderBy(qSubscribe.createdDate.desc());

        return query.fetch();
    }

    @Override
    public List<Cart> findCartsOrderByCreatedDateForMember(Long memberId) {

        return queryFactory
            .selectFrom(cart)
            .leftJoin(cart.video, video).fetchJoin()
            .leftJoin(video.channel, channel).fetchJoin()
            .leftJoin(channel.member, member).fetchJoin()
            .where(cart.member.memberId.eq(memberId))
            .orderBy(cart.createdDate.desc())
            .fetch();
    }

    @Override
    public List<Order> findOrdersOrderByCreatedDateForMember(Long memberId, int month) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = currentDateTime.minusMonths(month).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endDateTime = currentDateTime.withHour(23).withMinute(59).withSecond(59);

        return queryFactory
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
            .distinct()
            .fetch();
    }

    public List<Video> findPlaylistsOrderBySort(Long memberId, String sort) {
        OrderSpecifier<?> orderSpecifier;

        QVideo video = new QVideo("playlist");

        switch (sort) {
            case "name":
                orderSpecifier = video.videoName.asc();
                break;
            case "channel":
                orderSpecifier = video.channel.channelName.asc();
                break;
            case "star":
                orderSpecifier = video.star.desc();
                break;
            case "createdDate":
            default:
                orderSpecifier = video.createdDate.desc();
                break;
        }

        return queryFactory
            .selectFrom(video)
            .join(video.orderVideos, orderVideo)
            .join(orderVideo.order, order)
            .join(video.channel, channel).fetchJoin()
            .join(channel.member, member).fetchJoin()
            .where(
                order.member.memberId.eq(memberId)
                .and(order.orderStatus.eq(OrderStatus.COMPLETED))
            )
            .orderBy(orderSpecifier)
            .fetch();
    }

    public List<Watch> findWatchesForMember(Long memberId, int days) {
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = endDateTime.minusDays(days);

        return queryFactory
            .selectFrom(watch)
            .leftJoin(watch.video, video).fetchJoin()
            .leftJoin(video.channel, channel).fetchJoin()
            .where(
                watch.member.memberId.eq(memberId)
                    .and(watch.modifiedDate.between(startDateTime, endDateTime))
            )
            .orderBy(watch.modifiedDate.desc())
            .fetch();
    }

    public List<Reward> findRewardsByMemberId(Long memberId, Pageable pageable) {

        return queryFactory
            .selectDistinct(reward)
            .from(reward)
            .leftJoin(reward.video, video).fetchJoin()
            .leftJoin(reward.question, question).fetchJoin()
            .where(reward.member.memberId.eq(memberId))
            .orderBy(reward.createdDate.desc())
            .fetch();
    }
}
