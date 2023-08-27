package com.server.domain.video.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.video.entity.Video;

import javax.persistence.EntityManager;
import java.util.Optional;

import static com.server.domain.channel.entity.QChannel.channel;
import static com.server.domain.member.entity.QMember.member;
import static com.server.domain.question.entity.QQuestion.question;
import static com.server.domain.video.entity.QVideo.video;

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
}
