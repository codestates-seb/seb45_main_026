package com.server.domain.member.service.dto.response;

import com.server.domain.reward.entity.RewardType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewRewardsResponse {
	private Long rewardId;
	private RewardType rewardType;
	private Integer rewardPoint;
	private boolean isCanceled;
}
