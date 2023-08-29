package com.server.domain.announcement.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.announcement.entity.Announcement;
import com.server.domain.announcement.entity.QAnnouncement;
import com.server.domain.channel.entity.QChannel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.swing.text.html.Option;

import java.util.List;
import java.util.Optional;

import static com.server.domain.announcement.entity.QAnnouncement.*;
import static com.server.domain.channel.entity.QChannel.*;
import static com.server.domain.member.entity.QMember.member;

public class AnnouncementRepositoryImpl implements AnnouncementRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AnnouncementRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Announcement> findAnnouncementPageByMemberId(Long memberId, Pageable pageable) {

        List<Announcement> announcements = queryFactory
                .selectFrom(announcement)
                .join(announcement.channel, channel)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(announcement.createdDate.desc())
                .where(channel.member.memberId.eq(memberId)).fetch();

        long count = queryFactory
                .selectFrom(announcement)
                .join(announcement.channel, channel)
                .where(channel.member.memberId.eq(memberId)).fetchCount();

        return new PageImpl<>(announcements, pageable, count);
    }

    @Override
    public Optional<Announcement> findAnnouncementWithMember(Long announcementId) {

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(announcement)
                        .join(announcement.channel, channel).fetchJoin()
                        .join(channel.member, member).fetchJoin()
                        .where(announcement.announcementId.eq(announcementId))
                        .fetchOne());
    }
}
