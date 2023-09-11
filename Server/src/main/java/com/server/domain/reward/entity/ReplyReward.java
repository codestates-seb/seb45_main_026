package com.server.domain.reward.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("REPLY")
public class ReplyReward extends Reward {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    protected ReplyReward(Member member, Integer rewardPoint, Video video) {
        super(member, rewardPoint);
        this.video = video;
    }

    @Override
    public RewardType getRewardType() {
        return RewardType.REPLY;
    }
}
