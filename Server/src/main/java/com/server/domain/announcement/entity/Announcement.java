package com.server.domain.announcement.entity;

import com.server.domain.channel.entity.Channel;
import com.server.domain.report.entity.AnnouncementReport;
import com.server.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL)
    private List<AnnouncementReport> announcementReports = new ArrayList<>();

    public static Announcement createAnnouncement(Channel channel, String content) {

            return Announcement.builder()
                    .channel(channel)
                    .content(content)
                    .build();
    }

    public void updateAnnouncement(String content) {
        this.content = content == null ? this.content : content;
    }

    public Long getMemberId() {
        return this.channel.getMember().getMemberId();
    }
}
