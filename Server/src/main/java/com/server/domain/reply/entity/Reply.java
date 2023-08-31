package com.server.domain.reply.entity;

import com.server.domain.member.entity.Member;
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
    private LocalDateTime createdAt;


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


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long setReplyId(){
        return replyId;
    }
}

