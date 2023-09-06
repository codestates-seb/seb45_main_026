package com.server.domain.reward.service;

import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.question.entity.Question;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reward.entity.Rewardable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.member.entity.Member;
import com.server.domain.reward.entity.Reward;
import com.server.domain.reward.entity.RewardType;
import com.server.domain.reward.repository.RewardRepository;
import com.server.domain.video.entity.Video;

import java.util.List;



public interface RewardService {

	void createRewardIfNotPresent(Rewardable rewardable, Member member);

	void createQuestionRewardsIfNotPresent(List<Question> questions, Member member);

	void cancelReward(Order order);

	void cancelVideoReward(OrderVideo orderVideo);

}
