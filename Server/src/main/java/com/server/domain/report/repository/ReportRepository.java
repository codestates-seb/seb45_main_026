package com.server.domain.report.repository;

import com.server.domain.member.entity.Member;
import com.server.domain.report.entity.Report;
import com.server.domain.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByMemberAndVideo(Member member, Video video);

}