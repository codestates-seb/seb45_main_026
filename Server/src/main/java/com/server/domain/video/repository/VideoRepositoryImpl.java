package com.server.domain.video.repository;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.video.entity.Video;
import com.server.domain.video.entity.VideoStatus;
import com.server.domain.video.repository.dto.ChannelVideoGetDataRequest;
import com.server.domain.video.repository.dto.VideoGetDataRequest;
import org.springframework.data.domain.*;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.server.domain.cart.entity.QCart.cart;
import static com.server.domain.category.entity.QCategory.category;
import static com.server.domain.channel.entity.QChannel.channel;
import static com.server.domain.member.entity.QMember.member;
import static com.server.domain.order.entity.QOrder.order;
import static com.server.domain.order.entity.QOrderVideo.orderVideo;
import static com.server.domain.reply.entity.QReply.*;
import static com.server.domain.subscribe.entity.QSubscribe.subscribe1;
import static com.server.domain.video.entity.QVideo.video;
import static com.server.domain.videoCategory.entity.QVideoCategory.*;

public class VideoRepositoryImpl implements VideoRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public VideoRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.em = em;
    }

    @Override
    public Optional<Video> findVideoWithMember(Long videoId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(video)
                        .join(video.channel, channel).fetchJoin()
                        .join(channel.member, member).fetchJoin()
                        .where(video.videoId.eq(videoId))
                        .fetchOne()
        );
    }

    @Override
    public Optional<Video> findVideoDetail(Long videoId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(video)
                        .join(video.channel, channel).fetchJoin()
                        .join(channel.member, member).fetchJoin()
                        .leftJoin(video.videoCategories, videoCategory).fetchJoin()
                        .leftJoin(videoCategory.category, category).fetchJoin()
                        .where(video.videoId.eq(videoId))
                        .fetchOne()
        );
    }

    @Override
    public Optional<Video> findVideoDetailIncludeWithdrawal(Long videoId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(video)
                        .leftJoin(video.channel, channel).fetchJoin()
                        .leftJoin(channel.member, member).fetchJoin()
                        .leftJoin(video.videoCategories, videoCategory).fetchJoin()
                        .leftJoin(videoCategory.category, category).fetchJoin()
                        .where(video.videoId.eq(videoId))
                        .fetchOne()
        );
    }

    @Override
    public Boolean isPurchased(Long memberId, Long videoId) {
        Long result = queryFactory.select(orderVideo.orderVideoId)
                .from(member)
                .join(member.orders, order)
                .join(order.orderVideos, orderVideo)
                .where(member.memberId.eq(memberId)
                        .and(orderVideo.video.videoId.eq(videoId)
                                .and(orderVideo.orderStatus.eq(OrderStatus.COMPLETED))
                        )
                ).fetchOne();

        return result != null;
    }

    @Override
    public Boolean isReplied(Long memberId, Long videoId) {

        Long result = queryFactory.select(reply.replyId)
                .from(member)
                .join(member.replies, reply)
                .where(member.memberId.eq(memberId)
                        .and(reply.video.videoId.eq(videoId))
                ).fetchOne();

        return result != null;
    }

    @Override
    public Optional<Video> findVideoByNameWithMember(Long memberId, String videoName) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(video)
                        .where(video.videoName.eq(videoName))
                        .where(video.channel.channelId.eq(memberId))
                        .fetchOne()
        );
    }

    @Override
    public List<Long> findVideoIdInCart(Long memberId, List<Long> videoIds) {

        return queryFactory.select(video.videoId)
                .from(member)
                .join(member.carts, cart)
                .join(cart.video, video)
                .where(member.memberId.eq(memberId)
                        .and(video.videoId.in(videoIds))
                ).fetch();
    }

    @Override
    public Page<Video> findAllByCond(VideoGetDataRequest request) {

        JPAQuery<Video> query = queryFactory
                .selectFrom(video)
                .distinct()
                .offset(request.getPageable().getOffset())
                .limit(request.getPageable().getPageSize())
                .orderBy(getSort(request.getSort()))
                .where(
                        getCreateVideo(),
                        hasChannel(),
                        freeOrPaid(request.getFree()),
                        whetherIncludePurchased(request),
                        whetherIncludeOnlySubscribed(request)
                );

        JPAQuery<Long> countQuery = queryFactory
                .select(video.count())
                .from(video)
                .distinct()
                .where(
                        getCreateVideo(),
                        hasChannel(),
                        freeOrPaid(request.getFree()),
                        whetherIncludePurchased(request),
                        whetherIncludeOnlySubscribed(request)
                );

        if (hasCategory(request.getCategoryName())) {
            query
                    .join(video.videoCategories, videoCategory)
                    .join(videoCategory.category, category)
                    .where(category.categoryName.eq(request.getCategoryName()));

            countQuery
                    .join(video.videoCategories, videoCategory)
                    .join(videoCategory.category, category)
                    .where(category.categoryName.eq(request.getCategoryName()));
        }

        return new PageImpl<>(query.fetch(), request.getPageable(), countQuery.fetchOne());
    }

    @Override
    public Page<Video> findAllByCond(String keyword, VideoGetDataRequest request) {

        JPAQuery<Video> query = queryFactory
                .selectFrom(video)
                .distinct()
                .offset(request.getPageable().getOffset())
                .limit(request.getPageable().getPageSize())
                .where(
                        searchKeyword(keyword),
                        getCreateVideo(),
                        hasChannel(),
                        freeOrPaid(request.getFree()),
                        whetherIncludePurchased(request),
                        whetherIncludeOnlySubscribed(request)
                );

        JPAQuery<Long> countQuery = queryFactory
                .select(video.count())
                .from(video)
                .distinct()
                .where(
                        searchKeyword(keyword),
                        getCreateVideo(),
                        hasChannel(),
                        freeOrPaid(request.getFree()),
                        whetherIncludePurchased(request),
                        whetherIncludeOnlySubscribed(request)
                );

        if (hasCategory(request.getCategoryName())) {
            query
                    .join(video.videoCategories, videoCategory)
                    .join(videoCategory.category, category)
                    .where(category.categoryName.eq(request.getCategoryName()));

            countQuery
                    .join(video.videoCategories, videoCategory)
                    .join(videoCategory.category, category)
                    .where(category.categoryName.eq(request.getCategoryName()));
        }

        if(request.getSort() != null) {
            query.orderBy(getSort(request.getSort()));
        }

        return new PageImpl<>(query.fetch(), request.getPageable(), countQuery.fetchOne());
    }

    private BooleanExpression searchKeyword(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return null;
        }

        return Expressions.numberTemplate(Double.class,
                "function('matchVideo', {0}, {1})", video.videoName, keyword).gt(0);
    }

    @Override
    public Page<Video> findChannelVideoByCond(ChannelVideoGetDataRequest request) {

        JPAQuery<Video> query = queryFactory
                .selectFrom(video)
                .distinct()
                .offset(request.getPageable().getOffset()) // 페이징 조건 1
                .limit(request.getPageable().getPageSize()) // 페이징 조건 2
                .orderBy(getSort(request.getSort())) // 정렬 조건
                .where(
                        videoOwnerIs(request.getMemberId()), // 채널 주인의 비디오만 조회
                        getCreateVideo(), // 비디오 상태가 CREATED 인 것만 조회
                        freeOrPaid(request.getFree()), // 무료 비디오인지 유료 비디오인지 선택
                        whetherIncludePurchased(request) // 구매한 비디오를 포함할지 여부
                );

        JPAQuery<Long> countQuery = queryFactory.select(video.count()) // 쿼리문에 해당하는 count 쿼리
                .from(video)
                .where(
                        videoOwnerIs(request.getMemberId()),
                        getCreateVideo(),
                        freeOrPaid(request.getFree()),
                        whetherIncludePurchased(request)
                );

        if (hasCategory(request.getCategoryName())) { // 카테고리가 존재하면
            query // 카테고리에 해당하는 비디오만 조회 (카테고리 이름은 UNIQUE 인덱스로 포함함)
                    .join(video.videoCategories, videoCategory)
                    .join(videoCategory.category, category)
                    .where(category.categoryName.eq(request.getCategoryName()));

            countQuery
                    .join(video.videoCategories, videoCategory)
                    .join(videoCategory.category, category)
                    .where(category.categoryName.eq(request.getCategoryName()));
        }

        return new PageImpl<>(query.fetch(), request.getPageable(), countQuery.fetchOne());
    }



    private OrderSpecifier[] getSort(String sort) {

        List<OrderSpecifier<?>> orders = new ArrayList<>();
        orders.add(getOrderSpecifier(sort));
        orders.add(video.createdDate.desc());

        return orders.toArray(new OrderSpecifier[0]);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort) {

        if(sort == null || sort.equals("")) {
            return video.createdDate.desc();
        }

        switch (sort) {
            case "view":
                return video.view.desc();
            case "star":
                return video.star.desc();
            default:
                return video.createdDate.desc();
        }
    }

    private boolean hasCategory(String categoryName) {
        return categoryName != null;
    }

    private BooleanExpression videoOwnerIs(Long memberId) {
        return video.channel.channelId.eq(memberId);
    }

    private BooleanExpression getCreateVideo() {
        return video.videoStatus.eq(VideoStatus.CREATED);
    }

    private BooleanExpression hasChannel() {
        return video.channel.channelId.isNotNull();
    }

    private BooleanExpression freeOrPaid(Boolean free) {

        if(free == null) {
            return null;
        }

        if(free) {
            return video.price.eq(0);
        }

        return video.price.gt(0);
    }

    private Predicate whetherIncludePurchased(ChannelVideoGetDataRequest request) {

        if(request.isPurchased()) {
            return null;
        }

        List<Long> videoIds = getPurchasedVideoIds(request.getLoginMemberId());

        return video.videoId.notIn(videoIds);
    }

    private Predicate whetherIncludePurchased(VideoGetDataRequest request) {

        if(request.isPurchased()) {
            return null;
        }

        List<Long> videoIds = getPurchasedVideoIds(request.getLoginMemberId());

        return video.videoId.notIn(videoIds);
    }

    private Predicate whetherIncludeOnlySubscribed(VideoGetDataRequest request) {

        if(!request.isSubscribe()) {
            return null;
        }

        List<Long> channelIds = getSubscribeChannelIds(request.getLoginMemberId());

        return video.channel.channelId.in(channelIds);
    }

    private List<Long> getSubscribeChannelIds(Long loginMemberId) {

        return queryFactory // member 가 구독한 채널의 id
                .select(channel.channelId)
                .from(channel)
                .join(channel.subscribes, subscribe1)
                .join(subscribe1.member, member)
                .where(member.memberId.eq(loginMemberId))
                .fetch();
    }

    private List<Long> getPurchasedVideoIds(Long loginMemberId) {
        return queryFactory
                .select(video.videoId)
                .from(member)
                .join(member.orders, order)
                .join(order.orderVideos, orderVideo)
                .join(orderVideo.video, video)
                .where(orderVideo.orderStatus.eq(OrderStatus.COMPLETED))
                .where(member.memberId.eq(loginMemberId))
                .fetch();
    }


}
