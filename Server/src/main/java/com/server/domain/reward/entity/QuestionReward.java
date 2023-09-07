package com.server.domain.reward.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.question.entity.Question;
import com.server.domain.video.entity.Video;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DiscriminatorValue("QUIZ")
public class QuestionReward extends NewReward {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    protected QuestionReward(Member member, Integer rewardPoint, Question question) {
        super(member, rewardPoint);
        this.question = question;
    }

    @Override
    public RewardType getRewardType() {
        return RewardType.QUIZ;
    }

    public Video getVideo() {
        return question.getVideo();
    }
}
