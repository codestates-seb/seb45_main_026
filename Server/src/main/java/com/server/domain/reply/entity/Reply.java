package com.server.domain.reply.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.reply.dto.CreateReply;
import com.server.domain.reward.entity.Rewardable;
import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
public class Reply extends BaseEntity implements Rewardable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long replyId;

    @Column(nullable = false)
    private Integer star;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    public static Reply createReply(Member member, Video video, CreateReply createReply) {

        return Reply.builder()
                .content(createReply.getContent())
                .star(createReply.getStar())
                .member(member)
                .video(video)
                .build();
    }




    public void updateReply(String content, Integer star) {
        this.content = content == null ? this.content : content;
        this.star = star == null ? this.star : star;
    }

    public int getRewardPoint(){
        return 10;
    }
}