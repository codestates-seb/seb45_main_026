package com.server.domain.reward.service;

import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.question.entity.Question;
import com.server.domain.reward.entity.Rewardable;

import com.server.domain.member.entity.Member;

import java.util.List;



public interface RewardService {

	void createRewardIfNotPresent(Rewardable rewardable, Member member);

	void createQuestionRewardsIfNotPresent(List<Question> questions, Member member);

	void cancelOrderReward(Order order);

	void cancelVideoReward(OrderVideo orderVideo);

}
