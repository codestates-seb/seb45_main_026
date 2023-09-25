package com.server.domain.report.service;

import com.server.domain.member.entity.Member;
import com.server.domain.report.entity.Report;
import com.server.domain.report.entity.ReportType;
import com.server.domain.report.repository.ReportRepository;
import com.server.domain.video.entity.Video;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional
    public boolean reportVideo(Member member, Video video, String reportContent) {

        if(isAlreadyReport(member, video)) {
            return false;
        }

        reportRepository.save(Report.createVideoReport(member, video, reportContent, ReportType.VIDEO));

        return true;
    }

    private boolean isAlreadyReport(Member member, Video video) {
        return reportRepository.existsByMemberAndVideo(member, video);
    }
}
