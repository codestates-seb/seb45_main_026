package com.server.domain.reply.entity;

import com.server.domain.entity.BaseEntity;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
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
    @JoinColumn(name = "member_Id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_Id")
    private Video video;





}
