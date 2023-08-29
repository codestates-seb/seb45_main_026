package com.server.domain.channel.entity;

import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.member.entity.Member;
import com.server.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Channel extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long channelId;

    @Column(nullable = false)
    private String channelName;

    @Lob
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "channel")
    private List<Subscribe> subscribeList = new ArrayList<>();

    private int subscribers;

    private boolean isSubscribed; //구독여부

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Channel createChannel(String memberNickname) {
        return Channel.builder()
            .channelName(memberNickname)
            .build();
    }

    public void setChannelName(String channelName){
        this.channelName = channelName;
    }

    public void setDescription(String description){
        this.description = description;
    }


    public void setSubscribers(int subscribers){
        this.subscribers = subscribers;
    }

    public void setMember(Member member){
        this.member = member;
        if (this.member.getChannel() != this) {
            this.member.setChannel(this);
        }
    }

}
