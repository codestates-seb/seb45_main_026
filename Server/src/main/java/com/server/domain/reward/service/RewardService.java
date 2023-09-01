package com.server.domain.reward.service;

import com.server.domain.order.entity.Order;
import com.server.domain.question.entity.Question;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.member.entity.Member;
import com.server.domain.reward.entity.Reward;
import com.server.domain.reward.entity.RewardType;
import com.server.domain.reward.repository.RewardRepository;
import com.server.domain.video.entity.Video;

@Service
@Transactional
public class RewardService {

	private RewardRepository rewardRepository;

	public RewardService(RewardRepository rewardRepository) {
		this.rewardRepository = rewardRepository;
	}

	public float getVideoRewardPolicy() {
		return 0.01f;
	}

	public int getQuestionRewardPolicy() {
		return 10;
	}

	public void createVideoReward(Video video, Member member) {

		Reward reward = Reward.createReward(
				RewardType.VIDEO,
				(int) (video.getPrice() * getVideoRewardPolicy()),
				member,
				video
		);

		saveAndUpdateReward(reward, member);
	}

	public void createQuestionReward(Question question, Member member) {

		Reward reward = Reward.createReward(
				RewardType.QUIZ,
				getQuestionRewardPolicy(),
				member,
				question
		);

		saveAndUpdateReward(reward, member);
	}

	public void cancelReward(Order order) {

		order.refund();

		rewardRepository.findByOrderId(order.getOrderId())
				.forEach(Reward::cancelReward);
	}

	private void saveAndUpdateReward(Reward reward, Member member) {
		rewardRepository.save(reward);
		reward.updateMemberReward(member);
	}
}
