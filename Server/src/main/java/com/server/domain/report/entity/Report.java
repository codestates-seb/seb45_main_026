package com.server.domain.report.entity;

import com.server.domain.announcement.entity.Announcement;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.reply.entity.Reply;
import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Report extends BaseEntity {

    @Id
    @GeneratedValue
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Lob
    private String reportContent;

    protected Report(Member member, String reportContent) {
        this.member = member;
        this.reportContent = reportContent;
    }

    public static Report createVideoReport(Member member, Video video, String reportContent) {
        return new VideoReport(member, video, reportContent);
    }

    public static Report createReplyReport(Member member, Reply reply, String reportContent) {
        return new ReplyReport(member, reply, reportContent);
    }

    public static Report createAnnouncementReport(Member member, Announcement announcement, String reportContent) {
        return new AnnouncementReport(member, announcement, reportContent);
    }

    public static Report createChannelReport(Member member, Channel channel, String reportContent) {
        return new ChannelReport(member, channel, reportContent);
    }
}
