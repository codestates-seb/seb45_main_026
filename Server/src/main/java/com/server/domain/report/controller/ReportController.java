package com.server.domain.report.controller;

import com.server.domain.report.controller.dto.request.MemberBlockApiRequest;
import com.server.domain.report.entity.ReportType;
import com.server.domain.report.service.ReportService;
import com.server.domain.report.service.dto.response.*;
import com.server.domain.report.controller.dto.request.ReportSort;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RequestMapping("/reports")
@RestController
@Validated
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/videos")
    public ResponseEntity<ApiPageResponse<VideoReportResponse>> getReportVideos(
            @RequestParam(defaultValue = "1") @Positive(message = "{validation.positive}") int page,
            @RequestParam(defaultValue = "10") @Positive(message = "{validation.positive}") int size,
            @RequestParam(defaultValue = "last-reported-date") ReportSort sort) {

        Page<VideoReportResponse> reportVideos = reportService.getReportVideos(page - 1, size, sort.getSort());

        return ResponseEntity.ok(ApiPageResponse.ok(reportVideos, "비디오 신고 목록 조회 성공"));
    }

    @GetMapping("/replies")
    public ResponseEntity<ApiPageResponse<ReplyReportResponse>> getReportReplies(
            @RequestParam(defaultValue = "1") @Positive(message = "{validation.positive}") int page,
            @RequestParam(defaultValue = "10") @Positive(message = "{validation.positive}") int size,
            @RequestParam(defaultValue = "last-reported-date") ReportSort sort) {

        Page<ReplyReportResponse> reportReplies = reportService.getReportReplies(page - 1, size, sort.getSort());

        return ResponseEntity.ok(ApiPageResponse.ok(reportReplies, "댓글 신고 목록 조회 성공"));
    }

    @GetMapping("/channels")
    public ResponseEntity<ApiPageResponse<ChannelReportResponse>> getReportChannels(
            @RequestParam(defaultValue = "1") @Positive(message = "{validation.positive}") int page,
            @RequestParam(defaultValue = "10") @Positive(message = "{validation.positive}") int size,
            @RequestParam(defaultValue = "last-reported-date") ReportSort sort) {

        Page<ChannelReportResponse> reportChannels = reportService.getReportChannels(page - 1, size, sort.getSort());

        return ResponseEntity.ok(ApiPageResponse.ok(reportChannels, "채널 신고 목록 조회 성공"));
    }

    @GetMapping("/announcements")
    public ResponseEntity<ApiPageResponse<AnnouncementReportResponse>> getReportAnnouncements(
            @RequestParam(defaultValue = "1") @Positive(message = "{validation.positive}") int page,
            @RequestParam(defaultValue = "10") @Positive(message = "{validation.positive}") int size,
            @RequestParam(defaultValue = "last-reported-date") ReportSort sort) {

        Page<AnnouncementReportResponse> reportAnnouncements = reportService.getReportAnnouncements(page - 1, size, sort.getSort());

        return ResponseEntity.ok(ApiPageResponse.ok(reportAnnouncements, "공지사항 신고 목록 조회 성공"));
    }

    @GetMapping("/videos/{video-id}")
    public ResponseEntity<ApiPageResponse<ReportDetailResponse>> getReportVideoDetail(
            @PathVariable("video-id") @Positive(message = "{validation.positive}") Long videoId,
            @RequestParam(defaultValue = "1") @Positive(message = "{validation.positive}") int page,
            @RequestParam(defaultValue = "10") @Positive(message = "{validation.positive}") int size) {
        Page<ReportDetailResponse> reportVideoDetail = reportService.getReportDetails(videoId, page - 1, size, ReportType.VIDEO);

        return ResponseEntity.ok(ApiPageResponse.ok(reportVideoDetail, "비디오 신고 세부 내용 조회 성공"));
    }

    @GetMapping("/replies/{reply-id}")
    public ResponseEntity<ApiPageResponse<ReportDetailResponse>> getReportReplyDetail(
            @PathVariable("reply-id") @Positive(message = "{validation.positive}") Long replyId,
            @RequestParam(defaultValue = "1") @Positive(message = "{validation.positive}") int page,
            @RequestParam(defaultValue = "10") @Positive(message = "{validation.positive}") int size) {

        Page<ReportDetailResponse> reportVideoDetail = reportService.getReportDetails(replyId, page - 1, size, ReportType.REPLY);

        return ResponseEntity.ok(ApiPageResponse.ok(reportVideoDetail, "댓글 신고 세부 내용 조회 성공"));
    }

    @GetMapping("/channels/{channel-id}")
    public ResponseEntity<ApiPageResponse<ReportDetailResponse>> getReportChannelDetail(
            @PathVariable("channel-id") @Positive(message = "{validation.positive}") Long channelId,
            @RequestParam(defaultValue = "1") @Positive(message = "{validation.positive}") int page,
            @RequestParam(defaultValue = "10") @Positive(message = "{validation.positive}") int size) {

        Page<ReportDetailResponse> reportVideoDetail = reportService.getReportDetails(channelId, page - 1, size, ReportType.CHANNEL);

        return ResponseEntity.ok(ApiPageResponse.ok(reportVideoDetail, "채널 신고 세부 내용 조회 성공"));
    }

    @GetMapping("/announcements/{announcement-id}")
    public ResponseEntity<ApiPageResponse<ReportDetailResponse>> getReportAnnouncementDetail(
            @PathVariable("announcement-id") @Positive(message = "{validation.positive}") Long announcementId,
            @RequestParam(defaultValue = "1") @Positive(message = "{validation.positive}") int page,
            @RequestParam(defaultValue = "10") @Positive(message = "{validation.positive}") int size) {

        Page<ReportDetailResponse> reportVideoDetail = reportService.getReportDetails(announcementId, page - 1, size, ReportType.ANNOUNCEMENT);

        return ResponseEntity.ok(ApiPageResponse.ok(reportVideoDetail, "공지사항 신고 세부 내용 조회 성공"));
    }

    @PatchMapping("/members/{member-id}")
    public ResponseEntity<ApiSingleResponse<Boolean>> blockMember(
            @PathVariable("member-id") @Positive(message = "{validation.positive}") Long memberId,
            @RequestBody @Valid MemberBlockApiRequest request) {

        boolean result = reportService.blockMember(memberId, request.toServiceRequest());

        String message = result ? "회원 차단 성공" : "회원 차단 해제";

        return ResponseEntity.ok(ApiSingleResponse.ok(result, message));
    }
}
