package com.server.domain.reply.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Reply extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long replyId;

    @Column(nullable = false)
    private int star;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    public void setReplyId(Long replyId){
        this.replyId = replyId;
    }

    public void setStar(int star){
        this.star = star;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setMember(Member member){
        this.member = member;
    }

    public void setVideo(Video video){
        this.video = video;
    }

}

