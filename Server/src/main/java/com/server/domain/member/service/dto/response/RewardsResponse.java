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
			.map(reward -> {
				RewardsResponseBuilder builder = RewardsResponse.builder()
					.rewardType(reward.getRewardType())
					.rewardPoint(reward.getRewardPoint())
					.isCanceled(reward.isCanceled())
					.createdDate(reward.getCreatedDate());

				if (reward.getVideo() != null) {
					builder.videoId(reward.getVideo().getVideoId());
				} else {
					builder.videoId(0L);
				}

				if (reward.getQuestion() != null) {
					builder.questionId(reward.getQuestion().getQuestionId());
				} else {
					builder.questionId(0L);
				}

				return builder.build();
			})
			.collect(Collectors.toList());
	}
}
