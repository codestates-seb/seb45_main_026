package com.server.domain.reward.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.member.entity.Member;
import com.server.domain.member.entity.QMember;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.question.entity.QQuestion;
import com.server.domain.question.entity.Question;
import com.server.domain.reward.entity.*;
import com.server.domain.video.entity.QVideo;
import com.server.domain.video.entity.Video;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.server.domain.member.entity.QMember.*;
import static com.server.domain.order.entity.QOrder.*;
import static com.server.domain.order.entity.QOrderVideo.*;
import static com.server.domain.question.entity.QQuestion.question;
import static com.server.domain.reward.entity.QQuestionReward.questionReward;
import static com.server.domain.reward.entity.QReplyReward.*;
import static com.server.domain.reward.entity.QVideoReward.videoReward;
import static com.server.domain.video.entity.QVideo.video;

public class RewardRepositoryImpl implements RewardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public RewardRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Reward> findByOrderId(String orderId) {

        List<Long> videoIds = queryFactory.select(video.videoId)
                .from(orderVideo)
                .join(orderVideo.video, video)
                .join(orderVideo.order, order)
                .where(order.orderId.eq(orderId)
                        .and(orderVideo.orderStatus.eq(OrderStatus.COMPLETED)))
                .fetch();

        List<ReplyReward> replyRewards = queryFactory.selectFrom(replyReward)
                .join(replyReward.video, video)
                .where(video.videoId.in(videoIds))
                .fetch();

        List<QuestionReward> questionRewards = queryFactory.selectFrom(questionReward)
                .join(questionReward.question, question)
                .join(question.video, video)
                .where(video.videoId.in(videoIds))
                .fetch();

        List<VideoReward> videoRewards = queryFactory.selectFrom(videoReward)
                .join(videoReward.video, video)
                .where(video.videoId.in(videoIds))
                .fetch();

        List<Reward> rewards = new ArrayList<>();
        rewards.addAll(replyRewards);
        rewards.addAll(questionRewards);
        rewards.addAll(videoRewards);

        return rewards;
    }

    @Override
    public List<Reward> findByMemberAndVideoId(Long memberId, Long videoId) {

        List<ReplyReward> replyRewards = queryFactory.selectFrom(replyReward)
                .join(replyReward.member, member)
                .join(replyReward.video, video)
                .where(member.memberId.eq(memberId)
                        .and(video.videoId.eq(videoId)))
                .fetch();

        List<QuestionReward> questionRewards = queryFactory.selectFrom(questionReward)
                .join(questionReward.member, member)
                .join(questionReward.question, question)
                .join(question.video, video)
                .where(member.memberId.eq(memberId)
                        .and(video.videoId.eq(videoId)))
                .fetch();

        List<VideoReward> videoRewards = queryFactory.selectFrom(videoReward)
                .join(videoReward.member, member)
                .join(videoReward.video, video)
                .where(member.memberId.eq(memberId)
                        .and(video.videoId.eq(videoId)))
                .fetch();

        List<Reward> rewards = new ArrayList<>();
        rewards.addAll(replyRewards);
        rewards.addAll(questionRewards);
        rewards.addAll(videoRewards);

        return rewards.stream()
                .filter(reward -> !reward.isCanceled())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<QuestionReward> findByQuestionAndMember(Question question, Member member) {

        QuestionReward reward = queryFactory.selectFrom(questionReward)
                .join(questionReward.question, QQuestion.question)
                .join(questionReward.member, QMember.member)
                .where(QQuestion.question.eq(question)
                        .and(QMember.member.eq(member))
                        .and(questionReward.isCanceled.eq(false)))
                .fetchOne();

        return Optional.ofNullable(reward);
    }

    @Override
    public List<QuestionReward> findByQuestionsAndMember(List<Question> questions, Member member) {

        return queryFactory.selectFrom(questionReward)
                .join(questionReward.question, QQuestion.question)
                .join(questionReward.member, QMember.member)
                .where(QQuestion.question.in(questions)
                        .and(QMember.member.eq(member))
                        .and(questionReward.isCanceled.eq(false)))
                .fetch();
    }

    @Override
    public Optional<ReplyReward> findReplyRewardByVideoAndMember(Video video, Member member) {

        ReplyReward reward = queryFactory.selectFrom(replyReward)
                .join(replyReward.video, QVideo.video)
                .where(QVideo.video.eq(video)
                        .and(QMember.member.eq(member))
                        .and(questionReward.isCanceled.eq(false)))
                .fetchOne();

        return Optional.ofNullable(reward);
    }
}
