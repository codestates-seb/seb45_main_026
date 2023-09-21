package com.server.domain.reward.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.question.entity.Question;
import com.server.domain.reward.entity.QuestionReward;
import com.server.domain.reward.entity.ReplyReward;
import com.server.domain.reward.entity.Reward;
import com.server.domain.reward.entity.VideoReward;
import com.server.domain.video.entity.Video;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.server.domain.member.entity.QMember.member;
import static com.server.domain.order.entity.QOrderVideo.orderVideo;
import static com.server.domain.question.entity.QQuestion.question;
import static com.server.domain.reward.entity.QQuestionReward.questionReward;
import static com.server.domain.reward.entity.QReplyReward.replyReward;
import static com.server.domain.reward.entity.QReward.reward;
import static com.server.domain.reward.entity.QVideoReward.videoReward;
import static com.server.domain.video.entity.QVideo.video;

public class RewardRepositoryImpl implements RewardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public RewardRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<Reward> findByOrderIdOnce(Long memberId, String orderId) {

        JPAQuery<Long> videoIdsWhere = queryFactory.select(orderVideo.video.videoId)
                .from(orderVideo)
                .where(orderVideo.order.orderId.eq(orderId)
                        .and(orderVideo.orderStatus.eq(OrderStatus.COMPLETED)));

        return queryFactory
                .selectFrom(reward)
                .leftJoin(videoReward).on(reward.rewardId.eq(videoReward.rewardId))
                .leftJoin(replyReward).on(reward.rewardId.eq(replyReward.rewardId))
                .leftJoin(questionReward).on(reward.rewardId.eq(questionReward.rewardId))
                .leftJoin(questionReward.question, question)
                .where(reward.member.memberId.eq(memberId)
                        .and(
                                videoReward.video.videoId.in(videoIdsWhere)
                                        .or(question.video.videoId.in(videoIdsWhere))
                                        .or(replyReward.video.videoId.in(videoIdsWhere))
                        )
                ).fetch();
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
                .where(questionReward.question.eq(question)
                        .and(questionReward.member.eq(member))
                        .and(questionReward.isCanceled.eq(false)))
                .fetchOne();

        return Optional.ofNullable(reward);
    }

    @Override
    public List<QuestionReward> findByQuestionsAndMember(List<Question> questions, Member member) {

        return queryFactory.selectFrom(questionReward)
                .where(questionReward.question.in(questions)
                        .and(questionReward.member.eq(member))
                        .and(questionReward.isCanceled.eq(false)))
                .fetch();
    }

    @Override
    public Optional<ReplyReward> findReplyRewardByVideoAndMember(Video video, Member member) {

        ReplyReward reward = queryFactory.selectFrom(replyReward)
                .where(replyReward.video.eq(video)
                        .and(replyReward.member.eq(member))
                        .and(replyReward.isCanceled.eq(false)))
                .fetchOne();

        return Optional.ofNullable(reward);
    }
}
