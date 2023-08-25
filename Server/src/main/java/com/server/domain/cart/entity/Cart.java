package com.server.domain.cart.entity;

import com.server.domain.entity.BaseEntity;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import lombok.Getter;

import javax.persistence.*;
@Getter
@Entity
public class Cart extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_Id")
    private Video video;

    @Column(nullable = false)
    private int price;

}
