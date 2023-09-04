package com.server.domain.cart.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;
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
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(nullable = false)
    private int price;

    public static Cart createCart(Member member, Video video, int price) {
        Cart cart = new Cart();
        cart.member = member;
        cart.video = video;
        cart.price = price;
        return cart;
    }

}
