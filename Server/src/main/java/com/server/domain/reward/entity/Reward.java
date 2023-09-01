package com.server.domain.reward.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.server.domain.answer.entity.Answer;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Reward extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rewardId;

	@Column(nullable = false)
	private Long entityId;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	private RewardType rewardType;

	@Column(nullable = false)
	private Integer rewardPoint;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	 @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "video_id")
	 private Video video;

	 private boolean isCanceled;
	//
	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "answer_id")
	// private Answer answer;

	public static Reward createReward(Long entityId, RewardType rewardType,
										Integer rewardPoint, Member member, Video video) {
		return Reward.builder()
			.entityId(entityId)
			.rewardType(rewardType)
			.rewardPoint(rewardPoint)
			.member(member)
			.video(video)
			.build();
	}

	public void updateMemberReward(Member member) {
		member.addReward(this.rewardPoint);
	}

	public void cancelReward(Member member) {
		this.isCanceled = true;
		member.minusReward(this.rewardPoint);
	}

	public void cancelReward() {
		this.isCanceled = true;
		this.member.minusReward(this.rewardPoint);
	}
}
