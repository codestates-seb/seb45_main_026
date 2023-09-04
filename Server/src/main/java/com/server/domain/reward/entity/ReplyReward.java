package com.server.domain.reward.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.reply.entity.Reply;
import com.server.domain.video.entity.Video;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("REPLY")
public class ReplyReward extends NewReward {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply reply;

    protected ReplyReward(Member member, Integer rewardPoint, Reply reply) {
        super(member, rewardPoint);
        this.reply = reply;
    }

    @Override
    public RewardType getRewardType() {
        return RewardType.REPLY;
    }

    @Override
    public Long getEntityId() {
        return reply.getReplyId();
    }

    @Override
    public Video getVideo() {
        return reply.getVideo();
    }
}
