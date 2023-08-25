package com.server.domain.like.entity;

import com.server.domain.entity.BaseEntity;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import lombok.Getter;

import javax.persistence.*;
@Getter
@Entity
public class Like extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "videoId")
    private Video video;

}
