package com.server.domain.announcement.entity;

import com.server.domain.channel.entity.Channel;
import com.server.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@Entity
@Builder
public class Announcement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long announcementId;

    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    public static Announcement createAnnouncement(Channel channel, String content) {

            return Announcement.builder()
                    .channel(channel)
                    .content(content)
                    .build();
    }

    public void updateAnnouncement(String content) {
        this.content = content == null ? this.content : content;
    }
}
