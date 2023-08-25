package com.server.domain.watch.entity;

import com.server.domain.entity.BaseEntity;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Watch extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long watchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_Id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;


}
