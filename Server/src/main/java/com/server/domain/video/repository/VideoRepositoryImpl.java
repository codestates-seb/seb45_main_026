package com.server.domain.video.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.category.entity.QCategory;
import com.server.domain.member.entity.QMember;
import com.server.domain.video.entity.QVideo;
import com.server.domain.video.entity.Video;
import com.server.domain.videoCategory.entity.QVideoCategory;
import org.springframework.data.domain.*;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.server.domain.category.entity.QCategory.*;
import static com.server.domain.channel.entity.QChannel.channel;
import static com.server.domain.member.entity.QMember.member;
import static com.server.domain.question.entity.QQuestion.question;
import static com.server.domain.subscribe.entity.QSubscribe.subscribe1;
import static com.server.domain.video.entity.QVideo.video;
import static com.server.domain.videoCategory.entity.QVideoCategory.*;
import static io.lettuce.core.pubsub.PubSubOutput.Type.subscribe;

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
    public Page<Video> findAllByCategoryPaging(String categoryName, Pageable pageable, String sort, Long memberId, boolean isSubscribed) {

        QMember subscribedMember = new QMember("subscribedMember");

        List<OrderSpecifier<?>> orders = new ArrayList<>();
        orders.add(getOrderSpecifier(sort));
        orders.add(video.createdDate.desc());

        JPAQuery<Video> query = queryFactory
                .selectFrom(video)
                .distinct()
                .join(video.channel, channel).fetchJoin()
                .join(channel.member, member).fetchJoin()
                .join(video.videoCategories, videoCategory)
                .join(videoCategory.category, category)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orders.toArray(new OrderSpecifier[0]));

        if (categoryName != null && !categoryName.isEmpty()) {
            query.where(category.categoryName.eq(categoryName));
        }

        if(isSubscribed){
            query
                .join(channel.subscribes, subscribe1)
                    .join(subscribe1.member, subscribedMember) // subscribe1과 구독한 member를 join
                    .where(subscribedMember.memberId.eq(memberId));
        }

        JPAQuery<Video> countQuery = queryFactory.selectFrom(video)
                .join(video.channel, channel)
                .join(channel.member, member)
                .join(video.videoCategories, videoCategory)
                .join(videoCategory.category, category);

        if (categoryName != null && !categoryName.isEmpty()) {
            countQuery.where(category.categoryName.eq(categoryName));
        }

        if(isSubscribed){
            countQuery
                    .join(channel.subscribes, subscribe1)
                    .join(subscribe1.member, subscribedMember) // subscribe1과 구독한 member를 join
                    .where(subscribedMember.memberId.eq(memberId));
        }

        long totalCount = countQuery.fetchCount();

        return new PageImpl<>(query.fetch(), pageable, totalCount);
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
