package com.server.domain.report.repository;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.member.entity.Member;
import com.server.domain.report.entity.*;
import com.server.domain.report.repository.dto.response.*;
import com.server.domain.report.service.dto.response.ReportDetailResponse;
import com.server.domain.video.entity.Video;
import com.server.global.exception.businessexception.reportexception.ReportTypeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.server.domain.announcement.entity.QAnnouncement.announcement;
import static com.server.domain.channel.entity.QChannel.channel;
import static com.server.domain.member.entity.QMember.member;
import static com.server.domain.reply.entity.QReply.reply;
import static com.server.domain.report.entity.QAnnouncementReport.announcementReport;
import static com.server.domain.report.entity.QChannelReport.channelReport;
import static com.server.domain.report.entity.QReplyReport.replyReport;
import static com.server.domain.report.entity.QVideoReport.videoReport;
import static com.server.domain.video.entity.QVideo.video;

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

    @Override
    public Page<VideoReportData> findVideoReportDataByCond(Pageable pageable, String sort) {

        List<VideoReportData> videoReportDatas = queryFactory.select(
                        new QVideoReportData(
                                video.videoId,
                                video.videoName,
                                video.videoStatus,
                                videoReport.count(),
                                video.createdDate,
                                videoReport.createdDate.max()
                        )
                ).from(video)
                .join(video.videoReports, videoReport)
                .groupBy(video.videoId)
                .orderBy(getVideoReportSort(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(video.videoId.countDistinct())  // 중복 제거를 위해 countDistinct 사용
                .from(video)
                .where(video.videoReports.isNotEmpty());

        return new PageImpl<>(videoReportDatas, pageable, countQuery.fetchOne());
    }

    private OrderSpecifier[] getVideoReportSort(String sort) {

        List<OrderSpecifier<?>> orders = new ArrayList<>();

        OrderSpecifier<?> orderSpecifier;

        if(sort == null || sort.equals("")) {
            orderSpecifier = videoReport.createdDate.max().desc();
        }else{
            switch (sort) {
                case "createdDate":
                    orderSpecifier = video.createdDate.desc();
                    break;
                case "reportCount":
                    orderSpecifier = videoReport.count().desc();
                    break;
                default:
                    orderSpecifier = videoReport.createdDate.max().desc();
            }
        }

        orders.add(orderSpecifier);
        orders.add(videoReport.createdDate.max().desc());

        return orders.toArray(new OrderSpecifier[0]);
    }

    @Override
    public Page<ReplyReportData> findReplyReportDataByCond(Pageable pageable, String sort) {

        List<ReplyReportData> replyReportDatas = queryFactory.select(
                        new QReplyReportData(
                                video.videoId,
                                video.videoName,
                                reply.replyId,
                                reply.content,
                                replyReport.count(),
                                reply.createdDate,
                                replyReport.createdDate.max()
                        )
                ).from(reply)
                .join(reply.replyReports, replyReport)
                .join(reply.video, video)
                .groupBy(reply.replyId)
                .orderBy(getReplyReportSort(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(reply.replyId.countDistinct())
                .from(reply)
                .where(reply.replyReports.isNotEmpty());

        return new PageImpl<>(replyReportDatas, pageable, countQuery.fetchOne());
    }

    private OrderSpecifier[] getReplyReportSort(String sort) {

        List<OrderSpecifier<?>> orders = new ArrayList<>();

        OrderSpecifier<?> orderSpecifier;

        if(sort == null || sort.equals("")) {
            orderSpecifier = replyReport.createdDate.max().desc();
        }else{
            switch (sort) {
                case "createdDate":
                    orderSpecifier = reply.createdDate.desc();
                    break;
                case "reportCount":
                    orderSpecifier = replyReport.count().desc();
                    break;
                default:
                    orderSpecifier = replyReport.createdDate.max().desc();
            }
        }

        orders.add(orderSpecifier);
        orders.add(replyReport.createdDate.max().desc());

        return orders.toArray(new OrderSpecifier[0]);
    }

    @Override
    public Page<ChannelReportData> findChannelReportDataByCond(Pageable pageable, String sort) {

        List<ChannelReportData> channelReportDatas = queryFactory.select(
                        new QChannelReportData(
                                member.memberId,
                                channel.channelName,
                                channelReport.count(),
                                channel.createdDate,
                                channelReport.createdDate.max()
                        )
                ).from(channel)
                .join(channel.channelReports, channelReport)
                .join(channel.member, member)
                .groupBy(channel.channelId)
                .orderBy(getChannelReportSort(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(channel.channelId.countDistinct())
                .from(channel)
                .where(channel.channelReports.isNotEmpty());

        return new PageImpl<>(channelReportDatas, pageable, countQuery.fetchOne());
    }

    private OrderSpecifier[] getChannelReportSort(String sort) {

        List<OrderSpecifier<?>> orders = new ArrayList<>();

        OrderSpecifier<?> orderSpecifier;

        if(sort == null || sort.equals("")) {
            orderSpecifier = channelReport.createdDate.max().desc();
        }else{
            switch (sort) {
                case "createdDate":
                    orderSpecifier = channel.createdDate.desc();
                    break;
                case "reportCount":
                    orderSpecifier = channelReport.count().desc();
                    break;
                default:
                    orderSpecifier = channelReport.createdDate.max().desc();
            }
        }

        orders.add(orderSpecifier);
        orders.add(channelReport.createdDate.max().desc());

        return orders.toArray(new OrderSpecifier[0]);
    }

    @Override
    public Page<AnnouncementReportData> findAnnouncementReportDataByCond(Pageable pageable, String sort) {

        List<AnnouncementReportData> announcementReportDatas = queryFactory.select(
                        new QAnnouncementReportData(
                                announcement.announcementId,
                                announcement.content,
                                announcement.channel.channelId,
                                announcementReport.count(),
                                announcement.createdDate,
                                announcementReport.createdDate.max()
                        )
                ).from(announcement)
                .join(announcement.announcementReports, announcementReport)
                .groupBy(announcement.announcementId)
                .orderBy(getAnnouncementReportSort(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(announcement.announcementId.countDistinct())
                .from(announcement)
                .where(announcement.announcementReports.isNotEmpty());

        return new PageImpl<>(announcementReportDatas, pageable, countQuery.fetchOne());
    }

    private OrderSpecifier[] getAnnouncementReportSort(String sort) {

        List<OrderSpecifier<?>> orders = new ArrayList<>();

        OrderSpecifier<?> orderSpecifier;

        if(sort == null || sort.equals("")) {
            orderSpecifier = announcementReport.createdDate.max().desc();
        }else{
            switch (sort) {
                case "createdDate":
                    orderSpecifier = announcement.createdDate.desc();
                    break;
                case "reportCount":
                    orderSpecifier = announcementReport.count().desc();
                    break;
                default:
                    orderSpecifier = announcementReport.createdDate.max().desc();
            }
        }

        orders.add(orderSpecifier);
        orders.add(announcementReport.createdDate.max().desc());

        return orders.toArray(new OrderSpecifier[0]);
    }

    @Override
    public Page<? extends Report> findReportDetailByCond(Long entityId, Pageable pageable, ReportType reportType) {

        switch (reportType) {
            case VIDEO:
                return findVideoReportDetailByCond(entityId, pageable);
            case REPLY:
                return findReplyReportDetailByCond(entityId, pageable);
            case CHANNEL:
                return findChannelReportDetailByCond(entityId, pageable);
            case ANNOUNCEMENT:
                return findAnnouncementReportDetailByCond(entityId, pageable);
            default:
                throw new ReportTypeException();
        }
    }

    @Override
    public Page<Member> findMemberByKeyword(String keyword, Pageable pageable) {

        JPAQuery<Member> query = queryFactory.selectFrom(member)
                .join(member.channel, channel).fetchJoin()
                .where(LikeEmailOrNicknameOrChannelName(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        JPAQuery<Long> countQuery = queryFactory.select(member.count())
                .from(member)
                .where(LikeEmailOrNicknameOrChannelName(keyword));

        return new PageImpl<>(query.fetch(), pageable, countQuery.fetchOne());
    }

    @Override
    public Page<Video> findVideoByKeyword(String email, String keyword, Pageable pageable) {

        JPAQuery<Video> query = queryFactory
                .selectFrom(video)
                .leftJoin(video.channel, channel).fetchJoin()
                .leftJoin(channel.member, member).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(
                        eqEmail(email),
                        searchKeyword(keyword)
                );

        JPAQuery<Long> countQuery = queryFactory
                .select(video.count())
                .from(video)
                .where(
                        eqEmail(email),
                        searchKeyword(keyword)
                );

        return new PageImpl<>(query.fetch(), pageable, countQuery.fetchOne());
    }

    private BooleanExpression eqEmail(String email) {
        return email != null ? member.email.eq(email) : Expressions.asBoolean(true).isTrue();
    }

    private BooleanExpression searchKeyword(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return null;
        }

        return Expressions.numberTemplate(Double.class,
                "function('matchVideo', {0}, {1})", video.videoName, keyword).gt(0);
    }

    private BooleanExpression LikeEmailOrNicknameOrChannelName(String keyword) {

        if(keyword == null) {
            return Expressions.asBoolean(true).isTrue();
        }

        return member.nickname.contains(keyword)
                .or(member.email.contains(keyword))
                .or(channel.channelName.contains(keyword));
    }

    private Page<? extends Report> findVideoReportDetailByCond(Long videoId, Pageable pageable) {

        JPAQuery<VideoReport> query = queryFactory.selectFrom(videoReport)
                .leftJoin(videoReport.member, member).fetchJoin()
                .orderBy(videoReport.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(videoReport.video.videoId.eq(videoId));

        JPAQuery<Long> countQuery = queryFactory.select(videoReport.count())
                .from(videoReport)
                .where(videoReport.video.videoId.eq(videoId));

        return new PageImpl<>(query.fetch(), pageable, countQuery.fetchOne());
    }

    private Page<? extends Report> findReplyReportDetailByCond(Long replyId, Pageable pageable) {

        JPAQuery<ReplyReport> query = queryFactory.selectFrom(replyReport)
                .leftJoin(replyReport.member, member).fetchJoin()
                .orderBy(replyReport.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(replyReport.reply.replyId.eq(replyId));

        JPAQuery<Long> countQuery = queryFactory.select(replyReport.count())
                .from(replyReport)
                .where(replyReport.reply.replyId.eq(replyId));

        return new PageImpl<>(query.fetch(), pageable, countQuery.fetchOne());
    }

    private Page<? extends Report> findChannelReportDetailByCond(Long channelId, Pageable pageable) {

        JPAQuery<ChannelReport> query = queryFactory.selectFrom(channelReport)
                .leftJoin(channelReport.member, member).fetchJoin()
                .orderBy(channelReport.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(channelReport.channel.channelId.eq(channelId));

        JPAQuery<Long> countQuery = queryFactory.select(channelReport.count())
                .from(channelReport)
                .where(channelReport.channel.channelId.eq(channelId));

        return new PageImpl<>(query.fetch(), pageable, countQuery.fetchOne());
    }

    private Page<? extends Report> findAnnouncementReportDetailByCond(Long announcementId, Pageable pageable) {

        JPAQuery<AnnouncementReport> query = queryFactory.selectFrom(announcementReport)
                .leftJoin(announcementReport.member, member).fetchJoin()
                .orderBy(announcementReport.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(announcementReport.announcement.announcementId.eq(announcementId));

        JPAQuery<Long> countQuery = queryFactory.select(announcementReport.count())
                .from(announcementReport)
                .where(announcementReport.announcement.announcementId.eq(announcementId));

        return new PageImpl<>(query.fetch(), pageable, countQuery.fetchOne());
    }
}
