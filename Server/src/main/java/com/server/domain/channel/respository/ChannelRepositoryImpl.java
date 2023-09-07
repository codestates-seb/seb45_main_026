package com.server.domain.channel.respository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.QMember;

import javax.persistence.EntityManager;
import java.util.Optional;

import static com.server.domain.channel.entity.QChannel.channel;
import static com.server.domain.member.entity.QMember.*;

public class ChannelRepositoryImpl implements ChannelRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ChannelRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Channel> findByMember(Long memberId) {

        return Optional.ofNullable(queryFactory
                .selectFrom(channel)
                        .join(channel.member, member).fetchJoin()
                        .where(member.memberId.eq(memberId))
                .fetchOne());
    }
}
