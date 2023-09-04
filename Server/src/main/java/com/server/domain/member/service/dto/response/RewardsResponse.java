package com.server.domain.member.service.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.server.domain.question.entity.Question;
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

	public static Page<RewardsResponse> convert(Page<Reward> rewards) {
		return rewards.map(reward -> new RewardsResponse(
			reward.getVideo().getVideoId(),
			Optional.ofNullable(reward.getQuestion())
				.map(Question::getQuestionId)
				.orElse(null),
			reward.getRewardType(),
			reward.getRewardPoint(),
			reward.isCanceled(),
			reward.getCreatedDate()
		));
	}
}
