package com.server.domain.reward.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.member.entity.Member;
import com.server.domain.member.entity.QMember;
import com.server.domain.order.entity.QOrder;
import com.server.domain.order.entity.QOrderVideo;
import com.server.domain.question.entity.QQuestion;
import com.server.domain.question.entity.Question;
import com.server.domain.reply.entity.QReply;
import com.server.domain.reward.entity.*;
import com.server.domain.video.entity.QVideo;
import com.server.domain.video.entity.Video;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.server.domain.channel.entity.QChannel.channel;
import static com.server.domain.member.entity.QMember.*;
import static com.server.domain.order.entity.QOrder.*;
import static com.server.domain.order.entity.QOrderVideo.*;
import static com.server.domain.question.entity.QQuestion.question;
import static com.server.domain.reply.entity.QReply.reply;
import static com.server.domain.reward.entity.QNewReward.*;
import static com.server.domain.reward.entity.QQuestionReward.questionReward;
import static com.server.domain.reward.entity.QReplyReward.*;
import static com.server.domain.reward.entity.QReward.*;
import static com.server.domain.reward.entity.QVideoReward.videoReward;
import static com.server.domain.video.entity.QVideo.video;

public class NewRewardRepositoryImpl implements NewRewardRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public NewRewardRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<NewReward> findByOrderId(String orderId) {

        QVideo rv = new QVideo("rv");
        QVideo qv = new QVideo("qv");
        QVideo vv = new QVideo("vv");

        QOrderVideo rov = new QOrderVideo("rov");
        QOrderVideo qov = new QOrderVideo("qov");
        QOrderVideo vov = new QOrderVideo("vov");

        QOrder ro = new QOrder("ro");
        QOrder qo = new QOrder("qo");
        QOrder vo = new QOrder("vo");


        //방법 1
        //leftjoin 으로 한번에 다 가져오는 방법 -> 미친 방법
        /*List<NewReward> rewards = queryFactory.selectFrom(newReward)
                .join(newReward.member, member).fetchJoin()
                .join(member.channel, channel).fetchJoin()
                .leftJoin(replyReward).on(newReward.rewardId.eq(replyReward.rewardId))
                .leftJoin(replyReward.video, rv)
                .leftJoin(rv.orderVideos, rov)
                .leftJoin(rov.order, ro)
                .leftJoin(questionReward).on(newReward.rewardId.eq(questionReward.rewardId))
                .leftJoin(questionReward.question, question)
                .leftJoin(question.video, qv)
                .leftJoin(qv.orderVideos, qov)
                .leftJoin(qov.order, qo)
                .leftJoin(videoReward).on(newReward.rewardId.eq(videoReward.rewardId))
                .leftJoin(videoReward.video, vv)
                .leftJoin(vv.orderVideos, vov)
                .leftJoin(vov.order, vo)
                .where(ro.orderId.eq(orderId).or(qo.orderId.eq(orderId)).or(vo.orderId.eq(orderId)))
                .fetch();*/

        //방법 2
        //각각 하나하나 join 해서 총 쿼리 3번 보내는 방법 -> 최선인가..?
        List<ReplyReward> replyRewards = queryFactory.selectFrom(replyReward)
                .join(replyReward.video, video)
                .join(video.orderVideos, orderVideo)
                .join(orderVideo.order, order)
                .where(order.orderId.eq(orderId))
                .fetch();

        List<QuestionReward> questionRewards = queryFactory.selectFrom(questionReward)
                .join(questionReward.question, question)
                .join(question.video, video)
                .join(video.orderVideos, orderVideo)
                .join(orderVideo.order, order)
                .where(qo.orderId.eq(orderId))
                .fetch();

        List<VideoReward> videoRewards = queryFactory.selectFrom(videoReward)
                .join(videoReward.video, video)
                .join(video.orderVideos, orderVideo)
                .join(orderVideo.order, order)
                .where(qo.orderId.eq(orderId))
                .fetch();

        List<NewReward> rewards = new ArrayList<>();
        rewards.addAll(replyRewards);
        rewards.addAll(questionRewards);
        rewards.addAll(videoRewards);

        return rewards;
    }

    @Override
    public List<NewReward> findByMemberAndVideoId(Long memberId, Long videoId) {

        QVideo rv = new QVideo("rv");
        QVideo qv = new QVideo("qv");
        QVideo vv = new QVideo("vv");

        List<NewReward> rewards = queryFactory.selectFrom(newReward)
                .join(newReward.member, member).fetchJoin()
                .join(member.channel, channel).fetchJoin()
                .leftJoin(replyReward).on(newReward.rewardId.eq(replyReward.rewardId))
                .leftJoin(replyReward.video, rv)
                .leftJoin(questionReward).on(newReward.rewardId.eq(questionReward.rewardId))
                .leftJoin(questionReward.question, question)
                .leftJoin(question.video, qv)
                .leftJoin(videoReward).on(newReward.rewardId.eq(videoReward.rewardId))
                .leftJoin(videoReward.video, vv)
                .where(rv.videoId.eq(videoId).or(qv.videoId.eq(videoId)).or(vv.videoId.eq(videoId)))
                .where(member.memberId.eq(memberId))
                .fetch();

        return rewards;
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
