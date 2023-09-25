package com.server.domain.report.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

public class ReportRepositoryImpl implements ReportRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ReportRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public boolean existsByMemberIdAndVideoId(Long memberId, Long videoId) {
        return false;
    }

    @Override
    public boolean existsByMemberIdAndReplyId(Long memberId, Long replyId) {
        return false;
    }

    @Override
    public boolean existsByMemberIdAndChannelId(Long memberId, Long channelId) {
        return false;
    }

    @Override
    public boolean existsByMemberIdAndAnnouncementId(Long memberId, Long announcementId) {
        return false;
    }
}
