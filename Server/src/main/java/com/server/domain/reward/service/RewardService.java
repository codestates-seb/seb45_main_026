package com.server.domain.reward.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.answer.entity.Answer;
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

	public void createReward(Object entity, Video video, Member member) {
		if (entity instanceof Video) {
			Video purchaseVideo = (Video) entity;
			createRewardForVideo(purchaseVideo, member);
		}
		else if (entity instanceof Answer) {
			Answer answer = (Answer) entity;
			createRewardForAnswer(answer, video, member);
		}
	}

	private void createRewardForVideo(Video video, Member member) {
		Reward reward = Reward.createReward(
			video.getVideoId(),
			RewardType.VIDEO,
			(int) (video.getPrice() * 0.01),
			member,
			video
		);

		saveAndUpdateReward(reward, member);
	}

	private void createRewardForAnswer(Answer answer, Video video, Member member) {
		Reward reward = Reward.createReward(
			answer.getAnswerId(),
			RewardType.QUIZ,
			10,
			member,
			video
		);

		saveAndUpdateReward(reward, member);
	}

	private void saveAndUpdateReward(Reward reward, Member member) {
		rewardRepository.save(reward);
		reward.updateMemberReward(member);
	}
}
