package com.server.domain.report.controller;

import com.server.domain.report.service.ReportService;
import com.server.domain.report.service.dto.response.AdminMemberResponse;
import com.server.domain.report.service.dto.response.AdminVideoResponse;
import com.server.global.reponse.ApiPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/admin")
@Validated
public class AdminController {

    private final ReportService reportService;

    public AdminController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/members")
    public ResponseEntity<ApiPageResponse<AdminMemberResponse>> getMembers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") @Positive(message = "{validation.positive}") int page,
            @RequestParam(value = "size", defaultValue = "10") @Positive(message = "{validation.positive}") int size
    ) {
        Page<AdminMemberResponse> members = reportService.getMembers(keyword, page - 1, size);
        return ResponseEntity.ok(ApiPageResponse.ok(members, "멤버 목록 조회 성공"));
    }

    @GetMapping("/videos")
    public ResponseEntity<ApiPageResponse<AdminVideoResponse>> getVideos(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") @Positive(message = "{validation.positive}") int page,
            @RequestParam(value = "size", defaultValue = "10") @Positive(message = "{validation.positive}") int size
    ) {
        Page<AdminVideoResponse> videos = reportService.getVideos(email, keyword, page - 1, size);
        return ResponseEntity.ok(ApiPageResponse.ok(videos, "비디오 목록 조회 성공"));
    }
}
