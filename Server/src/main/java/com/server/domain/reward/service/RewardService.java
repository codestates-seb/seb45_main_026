package com.server.domain.reward.service;

import com.server.domain.order.entity.Order;
import com.server.domain.question.entity.Question;
import com.server.domain.reply.entity.Reply;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.member.entity.Member;
import com.server.domain.reward.entity.Reward;
import com.server.domain.reward.entity.RewardType;
import com.server.domain.reward.repository.RewardRepository;
import com.server.domain.video.entity.Video;

import java.util.List;

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

		rewardRepository.save(reward);
	}

	public void createReplyRewardIfNotPresent(Video video, Member member) {

		//todo: newReward 로 옮길 때 구현 예정
	}

	public void createQuestionRewardIfNotPresent(Question question, Member member) {

		rewardRepository.findByQuestionAndMember(question, member)
				.orElseGet(() -> createQuestionReward(question, member));
	}

	public void createQuestionRewardsIfNotPresent(List<Question> questions, Member member) {

		List<Reward> rewards = rewardRepository.findByQuestionsAndMember(questions, member);

		questions.stream()
				.filter(question -> rewards.stream()
						.noneMatch(reward -> reward.getQuestion().equals(question)))
				.forEach(question -> createQuestionReward(question, member));
	}

	public void cancelReward(Order order) {

		order.refund();

		rewardRepository.findByOrderId(order.getOrderId())
				.forEach(Reward::cancelReward);
	}

	private Reward createQuestionReward(Question question, Member member) {
		Reward reward = Reward.createReward(
		   RewardType.QUIZ,
		   getQuestionRewardPolicy(),
				member,
				question);
		return rewardRepository.save(reward);
	}
}
