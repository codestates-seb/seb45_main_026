package com.server.domain.report.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.report.entity.QVideoReport;

import javax.persistence.EntityManager;

import static com.server.domain.report.entity.QAnnouncementReport.announcementReport;
import static com.server.domain.report.entity.QChannelReport.channelReport;
import static com.server.domain.report.entity.QReplyReport.replyReport;
import static com.server.domain.report.entity.QVideoReport.*;

public class ReportRepositoryImpl implements ReportRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ReportRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public boolean existsByMemberIdAndVideoId(Long memberId, Long videoId) {

        Long id = queryFactory.select(videoReport.reportId)
                .from(videoReport)
                .where(videoReport.member.memberId.eq(memberId)
                        .and(videoReport.video.videoId.eq(videoId)))
                .fetchOne();

        return id != null;
    }

    @Override
    public boolean existsByMemberIdAndReplyId(Long memberId, Long replyId) {
        Long id = queryFactory.select(replyReport.reportId)
                .from(replyReport)
                .where(replyReport.member.memberId.eq(memberId)
                        .and(replyReport.reply.replyId.eq(replyId)))
                .fetchOne();

        return id != null;
    }

    @Override
    public boolean existsByMemberIdAndChannelId(Long memberId, Long channelId) {
        Long id = queryFactory.select(channelReport.reportId)
                .from(channelReport)
                .where(channelReport.member.memberId.eq(memberId)
                        .and(channelReport.channel.channelId.eq(channelId)))
                .fetchOne();

        return id != null;
    }

    @Override
    public boolean existsByMemberIdAndAnnouncementId(Long memberId, Long announcementId) {
        Long id = queryFactory.select(announcementReport.reportId)
                .from(announcementReport)
                .where(announcementReport.member.memberId.eq(memberId)
                        .and(announcementReport.announcement.announcementId.eq(announcementId)))
                .fetchOne();

        return id != null;
    }
}
