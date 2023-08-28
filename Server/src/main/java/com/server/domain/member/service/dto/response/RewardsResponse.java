package com.server.domain.member.service.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import com.server.domain.reward.entity.Reward;
import com.server.domain.reward.entity.RewardType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RewardsResponse {
	private Long entityId;
	private RewardType rewardType;
	private Integer rewardPoint;
	private LocalDateTime date;
}
