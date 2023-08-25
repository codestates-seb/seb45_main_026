package com.server.domain.channel.entity;

import com.server.domain.entity.BaseEntity;
import com.server.domain.Subscribe.entity.Subscribe;
import com.server.domain.member.entity.Member;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Getter
@Entity
public class Channel extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long channelId;

    @Column(nullable = false)
    private String channelName;

    @Lob
    private String description;

    @OneToMany(mappedBy = "channel")
    private List<Subscribe> subscribes = new ArrayList<>();

    private int subscribers;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


}
