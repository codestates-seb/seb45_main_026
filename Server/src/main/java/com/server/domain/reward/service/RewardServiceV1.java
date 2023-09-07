package com.server.domain.reward.service;

import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.question.entity.Question;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reward.entity.Reward;
import com.server.domain.reward.entity.RewardType;
import com.server.domain.reward.entity.Rewardable;
import com.server.domain.reward.repository.RewardRepository;
import com.server.domain.video.entity.Video;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class RewardServiceV1 implements RewardService {

	private RewardRepository rewardRepository;

	@Override
	public void createRewardIfNotPresent(Rewardable rewardable, Member member) {
		if (rewardable instanceof Video) {
			createVideoReward((Video) rewardable, member);
		} else if (rewardable instanceof Question) {
			createQuestionRewardIfNotPresent((Question) rewardable, member);
		} else if (rewardable instanceof Reply) {
			createReplyRewardIfNotPresent((Reply) rewardable, member);
		}
	}

	private void createVideoReward(Video video, Member member) {

		Reward reward = Reward.createReward(
				RewardType.VIDEO,
				video.getRewardPoint(),
				member,
				video
		);

		rewardRepository.save(reward);
	}

	private void createReplyRewardIfNotPresent(Reply reply, Member member) {

		//todo: 여기서는 구현 x (Reward 에는 Reply 를 저장할 수 없음)
	}

	private void createQuestionRewardIfNotPresent(Question question, Member member) {

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

		rewardRepository.findByOrderId(order.getOrderId())
				.forEach(Reward::cancelReward);
	}

	@Override
	public void cancelVideoReward(OrderVideo orderVideo) {

	}

	private Reward createQuestionReward(Question question, Member member) {
		Reward reward = Reward.createReward(
				RewardType.QUIZ,
				question.getRewardPoint(),
				member,
				question);
		return rewardRepository.save(reward);
	}
}
