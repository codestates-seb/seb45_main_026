package com.server.domain.video.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.member.entity.QMember;
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

    public VideoRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
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
    public Page<Video> findAllByCategoryPaging(VideoGetDataRequest request) {

        QMember subscribedMember = new QMember("subscribedMember");

        JPAQuery<Video> query = queryFactory
                .selectFrom(video)
                .distinct()
                .join(video.channel, channel).fetchJoin()
                .join(channel.member, member).fetchJoin()
                .offset(request.getPageable().getOffset())
                .limit(request.getPageable().getPageSize())
                .orderBy(getSort(request.getSort()))
                .where(video.videoStatus.eq(VideoStatus.CREATED).and(searchFree(request.getFree())));

        JPAQuery<Video> countQuery = queryFactory.selectFrom(video)
                .distinct()
                .where(video.videoStatus.eq(VideoStatus.CREATED).and(searchFree(request.getFree())));

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

        if(request.isSubscribe()){

            List<Long> channelIds = queryFactory // member 가 구독한 채널의 id
                    .select(channel.channelId)
                    .from(channel)
                    .join(channel.subscribes, subscribe1)
                    .join(subscribe1.member, subscribedMember)
                    .where(subscribedMember.memberId.eq(request.getLoginMemberId()))
                    .fetch();

            query
                    .join(video.channel, channel)
                    .where(channel.channelId.in(channelIds));

            countQuery
                    .join(video.channel, channel)
                    .where(channel.channelId.in(channelIds));
        }

        if(!request.isPurchased()) {

                List<Long> videoIds = queryFactory // member 가 구매한 비디오의 id
                        .select(video.videoId)
                        .from(member)
                        .join(member.orders, order)
                        .join(order.orderVideos, orderVideo)
                        .join(orderVideo.video, video)
                        .where(orderVideo.orderStatus.eq(OrderStatus.COMPLETED))
                        .where(member.memberId.eq(request.getLoginMemberId()))
                        .fetch();

                query
                        .where(video.videoId.notIn(videoIds));

                countQuery
                        .where(video.videoId.notIn(videoIds));
        }

        return new PageImpl<>(query.fetch(), request.getPageable(), countQuery.fetchCount());
    }

    private OrderSpecifier[] getSort(String sort) {

        List<OrderSpecifier<?>> orders = new ArrayList<>();
        orders.add(getOrderSpecifier(sort));
        orders.add(video.createdDate.desc());

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression searchFree(Boolean free) {

        if(free == null) {
            return null;
        }

        if(free) {
            return video.price.eq(0);
        }

        return video.price.gt(0);
    }

    private boolean hasCategory(String categoryName) {
        return categoryName != null;
    }

    @Override
    public List<Boolean> isPurchasedAndIsReplied(Long memberId, Long videoId) {

        Tuple tuple = queryFactory.select(video.videoId, reply.replyId)
                .from(member)
                .join(member.orders, order)
                .join(order.orderVideos, orderVideo)
                .join(orderVideo.video, video)
                .leftJoin(member.replies, reply).on(reply.video.videoId.eq(videoId))
                .where(member.memberId.eq(memberId).and(video.videoId.eq(videoId))).fetchOne();

        List<Boolean> results = new ArrayList<>();

        if(tuple == null) {
            results.add(false); // 구매 여부
            results.add(false); // 댓글 여부
        } else {
            results.add(tuple.get(video.videoId) != null);
            results.add(tuple.get(reply.replyId) != null);
        }

        return results;
    }

    @Override
    public Page<Video> findChannelVideoByCategoryPaging(ChannelVideoGetDataRequest request) {


        JPAQuery<Video> query = queryFactory
                .selectFrom(video)
                .distinct()
                .join(video.channel, channel).fetchJoin()
                .join(channel.member, member).fetchJoin()
                .offset(request.getPageable().getOffset())
                .limit(request.getPageable().getPageSize())
                .orderBy(getSort(request.getSort()))
                .where(member.memberId.eq(request.getMemberId()))
                .where(video.videoStatus.eq(VideoStatus.CREATED).and(searchFree(request.getFree())));


        JPAQuery<Video> countQuery = queryFactory.selectFrom(video)
                .join(video.channel, channel)
                .join(channel.member, member)
                .where(member.memberId.eq(request.getMemberId()))
                .where(video.videoStatus.eq(VideoStatus.CREATED).and(searchFree(request.getFree())));

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

        if(!request.isPurchased()) {

            List<Long> videoIds = queryFactory // member 가 구매한 비디오의 id
                    .select(video.videoId)
                    .from(member)
                    .join(member.orders, order)
                    .join(order.orderVideos, orderVideo)
                    .join(orderVideo.video, video)
                    .where(orderVideo.orderStatus.eq(OrderStatus.COMPLETED))
                    .where(member.memberId.eq(request.getLoginMemberId()))
                    .fetch();

            query
                    .where(video.videoId.notIn(videoIds));

            countQuery
                    .where(video.videoId.notIn(videoIds));
        }

        return new PageImpl<>(query.fetch(), request.getPageable(), countQuery.fetchCount());
    }

    @Override
    public Optional<Video> findVideoByNameWithMember(Long memberId, String videoName) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(video)
                        .join(video.channel, channel)
                        .join(channel.member, member)
                        .where(video.videoName.eq(videoName))
                        .where(member.memberId.eq(memberId))
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


}
