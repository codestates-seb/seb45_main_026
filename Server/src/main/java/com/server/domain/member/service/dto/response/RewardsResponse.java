package com.server.domain.member.service.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.server.domain.reward.entity.Reward;
import com.server.domain.reward.entity.RewardType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RewardsResponse {
	private Long videoId;
	private Long questionId;
	private RewardType rewardType;
	private Integer rewardPoint;
	private Boolean isCanceled;
	private LocalDateTime createdDate;

	public static List<RewardsResponse> convert(List<Reward> rewards) {
		return rewards.stream()
			.map(reward -> RewardsResponse.builder()
				.videoId(reward.getVideo().getVideoId())
				.questionId(reward.getQuestion().getQuestionId())
				.rewardType(reward.getRewardType())
				.rewardPoint(reward.getRewardPoint())
				.isCanceled(reward.isCanceled())
				.createdDate(reward.getCreatedDate())
				.build()
			)
			.collect(Collectors.toList());
	}
}
