package com.server.domain.report.entity;

import com.server.domain.member.entity.Member;
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
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Lob
    private String reportContent;

    @Builder
    private Report(Member member, Video video, String reportContent) {
        this.member = member;
        this.video = video;
        this.reportContent = reportContent;
    }

    public static Report createReport(Member member, Video video, String reportContent) {
        return Report.builder()
                .member(member)
                .video(video)
                .reportContent(reportContent)
                .build();
    }
}
