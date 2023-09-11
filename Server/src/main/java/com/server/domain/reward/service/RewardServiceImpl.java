package com.server.domain.reward.service;

import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.question.entity.Question;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reward.entity.*;
import com.server.domain.reward.repository.RewardRepository;
import com.server.domain.video.entity.Video;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class RewardServiceImpl implements RewardService {

	private final RewardRepository newRewardRepository;

	public RewardServiceImpl(RewardRepository rewardRepository) {
		this.newRewardRepository = rewardRepository;
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

	public void createQuestionRewardsIfNotPresent(List<Question> questions, Member member) {

		List<QuestionReward> rewards = newRewardRepository.findByQuestionsAndMember(questions, member);

		questions.stream()
				.filter(question -> rewards.stream()
						.noneMatch(reward -> reward.getQuestion().equals(question)))
				.forEach(question -> createQuestionReward(question, member));
	}

	public void cancelOrderReward(Order order) {

		Member member = order.getMember();

		List<Reward> rewards = newRewardRepository.findByOrderId(order.getOrderId());

		int refundRewardPoint = calculateRefundRewardFrom(rewards);

		if(!member.hasEnoughReward(refundRewardPoint)) {
			order.convertAmountToReward(refundRewardPoint - member.getReward());
		}

		rewards.forEach(Reward::cancelReward);
	}

	@Override
	public void cancelVideoReward(OrderVideo orderVideo) {

		Order order = orderVideo.getOrder();
		Member member = order.getMember();
		Video video = orderVideo.getVideo();

		List<Reward> rewards = newRewardRepository.findByMemberAndVideoId(
				member.getMemberId(),
				video.getVideoId());

		int refundRewardPoint = calculateRefundRewardFrom(rewards);

		if(!member.hasEnoughReward(refundRewardPoint)) {
			order.convertAmountToReward(refundRewardPoint - member.getReward());
		}

		rewards.forEach(Reward::cancelReward);
	}

	private int calculateRefundRewardFrom(List<Reward> rewards) {
		return rewards.stream()
				.mapToInt(Reward::getRewardPoint)
				.sum();
	}

	private void createReplyRewardIfNotPresent(Reply reply, Member member) {

		newRewardRepository.findReplyRewardByVideoAndMember(reply.getVideo(), member)
				.orElseGet(() -> createReplyReward(reply, member));
	}

	private void createQuestionRewardIfNotPresent(Question question, Member member) {

		newRewardRepository.findByQuestionAndMember(question, member)
				.orElseGet(() -> createQuestionReward(question, member));
	}

	private void createVideoReward(Video video, Member member) {

		Reward reward = Reward.createReward(
				video.getRewardPoint(),
				member,
				video
		);

		newRewardRepository.save(reward);
	}

	private QuestionReward createQuestionReward(Question question, Member member) {
		QuestionReward reward = (QuestionReward) Reward.createReward(
				question.getRewardPoint(),
				member,
				question);

		return newRewardRepository.save(reward);
	}

	private ReplyReward createReplyReward(Reply reply, Member member) {
		ReplyReward reward = (ReplyReward) Reward.createReward(
				reply.getRewardPoint(),
				member,
				reply);

		return newRewardRepository.save(reward);
	}
}
