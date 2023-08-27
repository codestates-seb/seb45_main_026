package com.server.domain.channel.entity;

import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.member.entity.Member;
import com.server.global.entity.BaseEntity;
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

    private String imageUrl;

    @OneToMany(mappedBy = "channel")
    private List<Subscribe> subscribeList = new ArrayList<>();

    private int subscribers;

    private boolean isSubscribed; //구독여부

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void setChannelName(String channelName){
        this.channelName = channelName;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void subscribers(int subscribers){
        this.subscribers = subscribers;
    }

    public void setMemberId(Long memberId){
        setMemberId(memberId);
    }

    public void setSubscribed(boolean subscribed) {
    }

    public void incrementSubscribers(){
        subscribers++;
    }
    public void decrementSubscribers(){
        if(subscribers > 0){
            subscribers--;
        }
    }


}
