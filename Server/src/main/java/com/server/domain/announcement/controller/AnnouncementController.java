package com.server.domain.announcement.controller;

import com.server.domain.announcement.controller.dto.request.AnnouncementUpdateApiRequest;
import com.server.domain.announcement.service.AnnouncementService;
import com.server.domain.announcement.service.dto.response.AnnouncementResponse;
import com.server.domain.report.controller.dto.request.ReportCreateApiRequest;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/announcements")
@Validated
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping("/{announcement-id}")
    public ResponseEntity<ApiSingleResponse<AnnouncementResponse>> getAnnouncement(
            @PathVariable("announcement-id") @Positive(message = "{validation.positive}") Long announcementId) {

        AnnouncementResponse response = announcementService.getAnnouncement(announcementId);

        return ResponseEntity.ok(ApiSingleResponse.ok(response, "공지사항 조회 성공"));
    }

    @PatchMapping("/{announcement-id}")
    public ResponseEntity<Void> updateAnnouncement(
            @PathVariable("announcement-id") @Positive(message = "{validation.positive}") Long announcementId,
            @RequestBody @Valid AnnouncementUpdateApiRequest request,
            @LoginId Long loginMemberId
    ) {

        announcementService.updateAnnouncement(loginMemberId, request.toServiceRequest(announcementId));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{announcement-id}")
    public ResponseEntity<Void> deleteAnnouncement(
            @PathVariable("announcement-id") @Positive(message = "{validation.positive}") Long announcementId,
            @LoginId Long loginMemberId) {

        announcementService.deleteAnnouncement(loginMemberId, announcementId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{announcement-id}/reports")
    public ResponseEntity<ApiSingleResponse<Boolean>> reportAnnouncement(
            @PathVariable("announcement-id") @Positive(message = "{validation.positive}") Long announcementId,
            @RequestBody @Valid ReportCreateApiRequest request,
            @LoginId Long loginMemberId) {

        boolean result = announcementService.reportAnnouncement(loginMemberId, announcementId, request.getReportContent());

        String message = result ? "공지사항 신고 성공" : "이미 신고한 공지사항입니다.";

        return ResponseEntity.ok(ApiSingleResponse.ok(result, message));
    }
}
