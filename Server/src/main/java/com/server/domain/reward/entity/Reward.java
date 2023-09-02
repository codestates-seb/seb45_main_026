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
import com.server.domain.question.entity.Question;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id")
	private Question question;

	public static Reward createReward(RewardType rewardType,
										Integer rewardPoint, Member member, Video video) {

		member.addReward(rewardPoint);

		return Reward.builder()
			.rewardType(rewardType)
			.rewardPoint(rewardPoint)
			.member(member)
			.video(video)
			.build();
	}

	public static Reward createReward(RewardType rewardType,
		Integer rewardPoint, Member member, Question question) {

		member.addReward(rewardPoint);

		return Reward.builder()
			.rewardType(rewardType)
			.rewardPoint(rewardPoint)
			.member(member)
			.question(question)
			.video(question.getVideo())
			.build();
	}

	public void cancelReward() {
		this.isCanceled = true;
		this.member.minusReward(this.rewardPoint);
	}
}
