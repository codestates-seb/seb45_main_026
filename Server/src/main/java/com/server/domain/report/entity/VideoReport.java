package com.server.domain.report.entity;

import com.server.domain.announcement.entity.Announcement;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoReport extends Report {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    protected VideoReport(Member member, Video video, String reportContent) {
        super(member, reportContent);
        this.video = video;
    }
}
