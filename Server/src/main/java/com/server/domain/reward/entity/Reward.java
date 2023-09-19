package com.server.domain.reward.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.question.entity.Question;
import com.server.domain.reply.entity.Reply;
import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;
import com.server.global.exception.businessexception.rewardexception.RewardNotValidException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "rewardType")
public abstract class Reward extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long rewardId;

	@Column(nullable = false)
	protected Integer rewardPoint;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	protected Member member;

	protected boolean isCanceled;

	public abstract RewardType getRewardType();

	protected Reward(Member member, Integer rewardPoint) {
		this.member = member;
		this.rewardPoint = rewardPoint;
	}

	public static Reward createReward(Integer rewardPoint,
									  Member member,
									  Rewardable entity) {

		member.addReward(rewardPoint);
		member.addGradePoint(rewardPoint);

		if (entity instanceof Video) {
			return new VideoReward(member, rewardPoint, (Video) entity);
		} else if (entity instanceof Question) {
			return new QuestionReward(member, rewardPoint, (Question) entity);
		} else if (entity instanceof Reply) {
			return new ReplyReward(member, rewardPoint, ((Reply) entity).getVideo());
		} else {
			throw new RewardNotValidException();
		}
	}

	public void cancelReward() {
		this.isCanceled = true;
		this.member.minusReward(this.rewardPoint);
		this.member.minusGradePoint(this.rewardPoint);
	}
}
