package com.server.domain.Subscribe.entity;

import com.server.domain.entity.BaseEntity;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import lombok.Getter;

import javax.persistence.*;
@Getter
@Entity
public class Subscribe extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long subscribe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_Id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_Id")
    private Channel channel;








}
