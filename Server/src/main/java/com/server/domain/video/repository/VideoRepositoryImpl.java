package com.server.domain.video.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.member.entity.QMember;
import com.server.domain.video.entity.Video;
import com.server.domain.video.entity.VideoStatus;
import com.server.domain.video.repository.dto.VideoGetDataRequest;
import org.springframework.data.domain.*;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public Page<Video> findAllByCategoryPaging(
            VideoGetDataRequest request) {

        QMember subscribedMember = new QMember("subscribedMember");

        List<OrderSpecifier<?>> orders = new ArrayList<>();
        orders.add(getOrderSpecifier(request.getSort()));
        orders.add(video.createdDate.desc());

        JPAQuery<Video> query = queryFactory
                .selectFrom(video)
                .distinct()
                .join(video.channel, channel).fetchJoin()
                .join(channel.member, member).fetchJoin()
                .offset(request.getPageable().getOffset())
                .limit(request.getPageable().getPageSize())
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .where(video.videoStatus.eq(VideoStatus.CREATED).and(searchFree(request.getFree())));


        JPAQuery<Video> countQuery = queryFactory.selectFrom(video)
                .distinct()
                .join(video.channel, channel)
                .join(channel.member, member)
                .where(video.videoStatus.eq(VideoStatus.CREATED).and(searchFree(request.getFree())));

        if (hasCategory(request)) {
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
            query
                    .join(channel.subscribes, subscribe1)
                    .join(subscribe1.member, subscribedMember) // subscribe1과 구독한 member를 join
                    .where(subscribedMember.memberId.eq(request.getLoginMemberId()));

            countQuery
                    .join(channel.subscribes, subscribe1)
                    .join(subscribe1.member, subscribedMember) // subscribe1과 구독한 member를 join
                    .where(subscribedMember.memberId.eq(request.getLoginMemberId()));
        }

        long totalCount = countQuery.fetchCount();

        return new PageImpl<>(query.fetch(), request.getPageable(), totalCount);
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

    private boolean hasCategory(VideoGetDataRequest request) {
        return request.getCategoryName() != null && !request.getCategoryName().isEmpty();
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
    public Page<Video> findChannelVideoByCategoryPaging(Long memberId,
                                                        String categoryName,
                                                        Pageable pageable,
                                                        String sort,
                                                        Boolean free) {

        List<OrderSpecifier<?>> orders = new ArrayList<>();
        orders.add(getOrderSpecifier(sort));
        orders.add(video.createdDate.desc());

        JPAQuery<Video> query = queryFactory
                .selectFrom(video)
                .distinct()
                .join(video.channel, channel).fetchJoin()
                .join(channel.member, member).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(member.memberId.eq(memberId))
                .where(video.videoStatus.eq(VideoStatus.CREATED).and(searchFree(free)))
                .orderBy(orders.toArray(new OrderSpecifier[0]));

        if (categoryName != null && !categoryName.isEmpty()) {
            query
                    .join(video.videoCategories, videoCategory)
                    .join(videoCategory.category, category)
                    .where(category.categoryName.eq(categoryName));
        }

        JPAQuery<Video> countQuery = queryFactory.selectFrom(video)
                .join(video.channel, channel)
                .join(channel.member, member)
                .where(member.memberId.eq(memberId))
                .where(video.videoStatus.eq(VideoStatus.CREATED).and(searchFree(free)));

        if (categoryName != null && !categoryName.isEmpty()) {
            countQuery
                    .join(video.videoCategories, videoCategory)
                    .join(videoCategory.category, category)
                    .where(category.categoryName.eq(categoryName));
        }

        long totalCount = countQuery.fetchCount();

        return new PageImpl<>(query.fetch(), pageable, totalCount);
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
