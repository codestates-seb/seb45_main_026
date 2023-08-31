package com.server.domain.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.member.entity.QMember;
import com.server.domain.order.entity.QOrder;

import javax.persistence.EntityManager;

import java.util.List;

import static com.server.domain.cart.entity.QCart.cart;
import static com.server.domain.member.entity.QMember.*;
import static com.server.domain.order.entity.QOrder.*;
import static com.server.domain.order.entity.QOrderVideo.orderVideo;
import static com.server.domain.video.entity.QVideo.video;

public class OrderRepositoryImpl implements OrderRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public OrderRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Long deleteCartByMemberAndOrderId1(Long memberId, String orderId) {

        return queryFactory.delete(cart)
                .where(cart.video.orderVideos.any().order.orderId.eq(orderId)
                        .and(cart.member.memberId.eq(memberId))
                ).execute();
    }

    public Long deleteCartByMemberAndOrderId2(Long memberId, String orderId) {

        List<Long> deleteCartIds = queryFactory.select(cart.cartId)
                .from(cart)
                .join(cart.member, member)
                .join(cart.video, video)
                .join(video.orderVideos, orderVideo)
                .where(orderVideo.order.orderId.eq(orderId))
                .where(member.memberId.eq(memberId))
                .fetch();

        return queryFactory.delete(cart)
                .where(cart.cartId.in(deleteCartIds))
                .execute();
    }
}
