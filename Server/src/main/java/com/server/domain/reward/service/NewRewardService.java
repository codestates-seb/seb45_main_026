package com.server.domain.reward.service;

import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.question.entity.Question;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reward.entity.*;
import com.server.domain.reward.repository.NewRewardRepository;
import com.server.domain.reward.repository.RewardRepository;
import com.server.domain.video.entity.Video;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class NewRewardService implements RewardService {

	private NewRewardRepository newRewardRepository;

	public NewRewardService(NewRewardRepository newRewardRepository) {
		this.newRewardRepository = newRewardRepository;
	}

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

		NewReward reward = NewReward.createReward(
				video.getRewardPoint(),
				member,
				video
		);

		newRewardRepository.save(reward);
	}

	private void createReplyRewardIfNotPresent(Reply reply, Member member) {

		newRewardRepository.findReplyRewardByVideoAndMember(reply.getVideo(), member)
				.orElseGet(() -> createReplyReward(reply, member));
	}

	private void createQuestionRewardIfNotPresent(Question question, Member member) {

		newRewardRepository.findByQuestionAndMember(question, member)
				.orElseGet(() -> createQuestionReward(question, member));
	}

	public void createQuestionRewardsIfNotPresent(List<Question> questions, Member member) {

		List<QuestionReward> rewards = newRewardRepository.findByQuestionsAndMember(questions, member);

		questions.stream()
				.filter(question -> rewards.stream()
						.noneMatch(reward -> reward.getQuestion().equals(question)))
				.forEach(question -> createQuestionReward(question, member));
	}

	public void cancelReward(Order order) {

		order.refund();

		newRewardRepository.findByOrderId(order.getOrderId())
				.forEach(NewReward::cancelReward);
	}

	private ReplyReward createReplyReward(Reply reply, Member member) {
		ReplyReward reward = (ReplyReward) NewReward.createReward(
				reply.getRewardPoint(),
				member,
				reply);
		return newRewardRepository.save(reward);
	}

	private QuestionReward createQuestionReward(Question question, Member member) {
		QuestionReward reward = (QuestionReward) NewReward.createReward(
				question.getRewardPoint(),
				member,
				question);
		return newRewardRepository.save(reward);
	}
}
