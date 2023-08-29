package com.server.domain.subscribe.entity;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.global.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Subscribe extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long subscribe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    public Subscribe(Member member, Channel channel) {
        this.member = member;
        this.channel = channel;
    }
}