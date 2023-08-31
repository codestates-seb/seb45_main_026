package com.server.domain.member.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.dto.MemberVideoData;
import com.server.domain.member.repository.dto.QMemberVideoData;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.subscribe.entity.QSubscribe;
import com.server.domain.video.entity.Video;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.server.domain.channel.entity.QChannel.channel;
import static com.server.domain.member.entity.QMember.*;
import static com.server.domain.order.entity.QOrder.*;
import static com.server.domain.order.entity.QOrderVideo.*;
import static com.server.domain.subscribe.entity.QSubscribe.*;
import static com.server.domain.video.entity.QVideo.*;
import static io.lettuce.core.pubsub.PubSubOutput.Type.subscribe;

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
}
