package com.server.domain.channel.entity;

import com.server.domain.announcement.entity.Announcement;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
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

    @OneToMany(mappedBy = "channel")
    private List<Subscribe> subscribes = new ArrayList<>();

    private int subscribers;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
    private List<Video> videos = new ArrayList<>();

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
    private List<Announcement> announcements = new ArrayList<>();

    public static Channel createChannel(String memberNickname) {
        return Channel.builder()
                .channelName(memberNickname)
                .build();
    }

    public void updateChannel(String channelName, String description){
        this.channelName = channelName;
        this.description = description;
    }



    public void setMember(Member member){
        this.member = member;
        if (this.member.getChannel() != this) {
            this.member.setChannel(this);
        }
    }

    public void addSubscribers(int subscribers){
        this.subscribers += subscribers;
    }

    public void decreaseSubscribers(int subscribers){
        this.subscribers -= subscribers;
    }





}
