package com.server.domain.reward.repository;

import com.server.domain.member.entity.Member;
import com.server.domain.question.entity.Question;
import com.server.domain.reward.entity.Reward;
import com.server.domain.reward.entity.QuestionReward;
import com.server.domain.reward.entity.ReplyReward;
import com.server.domain.video.entity.Video;

import java.util.List;
import java.util.Optional;

public interface RewardRepositoryCustom {

    List<Reward> findByOrderId(String orderId);

    List<Reward> findByMemberAndVideoId(Long memberId, Long videoId);

    Optional<QuestionReward> findByQuestionAndMember(Question question, Member member);

    List<QuestionReward> findByQuestionsAndMember(List<Question> questions, Member member);

    Optional<ReplyReward> findReplyRewardByVideoAndMember(Video video, Member member);
}
