package com.server.domain.report.controller;

import com.server.domain.report.service.ReportService;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

@RequestMapping("/reports")
@RestController
@Validated
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/videos/{video-id}")
    public ResponseEntity<ApiSingleResponse<Boolean>> reportVideo(
            @PathVariable("video-id") @Positive(message = "{validation.positive}") Long videoId,
            @LoginId Long loginMemberId
    ){
        return null;
    }

    @PostMapping("/replies/{reply-id}")
    public ResponseEntity<ApiSingleResponse<Boolean>> reportReply(
            @PathVariable("reply-id") @Positive(message = "{validation.positive}") Long replyId,
            @LoginId Long loginMemberId
    ){
        return null;
    }

    @PostMapping("/channels/{channel-id}")
    public ResponseEntity<ApiSingleResponse<Boolean>> reportChannel(
            @PathVariable("channel-id") @Positive(message = "{validation.positive}") Long channelId,
            @LoginId Long loginMemberId
    ){
        return null;
    }

    @PostMapping("/announcements/{announcement-id}")
    public ResponseEntity<ApiSingleResponse<Boolean>> reportAnnouncement(
            @PathVariable("announcement-id") @Positive(message = "{validation.positive}") Long announcementId,
            @LoginId Long loginMemberId
    ){
        return null;
    }
}
