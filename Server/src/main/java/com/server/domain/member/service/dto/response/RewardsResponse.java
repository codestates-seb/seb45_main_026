package com.server.domain.member.service.dto.response;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;

import com.server.domain.reward.entity.Reward;
import com.server.domain.reward.entity.RewardType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RewardsResponse {
	private Long rewardId;
	private RewardType rewardType;
	private Integer rewardPoint;
	private boolean isCanceled;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;

	public static Page<RewardsResponse> convert(Page<Reward> rewards) {
		return rewards.map(
			newReward -> RewardsResponse.builder()
				.rewardId(newReward.getRewardId())
				.rewardType(newReward.getRewardType())
				.rewardPoint(newReward.getRewardPoint())
				.isCanceled(newReward.isCanceled())
				.createdDate(newReward.getCreatedDate())
				.modifiedDate(newReward.getModifiedDate())
				.build()
		);
	}
}
