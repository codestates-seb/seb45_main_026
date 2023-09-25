package com.server.domain.channel.controller;

import com.server.domain.announcement.service.AnnouncementService;
import com.server.domain.announcement.service.dto.response.AnnouncementResponse;
import com.server.domain.channel.controller.dto.request.CreateAnnouncementApiRequest;
import com.server.domain.channel.service.ChannelService;
import com.server.domain.channel.service.dto.ChannelInfo;
import com.server.domain.channel.service.dto.ChannelUpdate;
import com.server.domain.channel.service.dto.request.ChannelVideoGetServiceRequest;
import com.server.domain.channel.service.dto.response.ChannelVideoResponse;
import com.server.domain.report.controller.dto.request.ReportCreateApiRequest;
import com.server.domain.video.controller.dto.request.VideoSort;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;


@RestController
@RequestMapping("/channels")
@Validated
public class ChannelController {

    private final ChannelService channelService;
    private final AnnouncementService announcementService;

    public ChannelController(ChannelService channelService, AnnouncementService announcementService){
        this.channelService = channelService;
        this.announcementService = announcementService;
    }

    // 채널 조회
    @GetMapping("/{member-id}")
    public ResponseEntity<ApiSingleResponse<ChannelInfo>> getChannel(
            @PathVariable("member-id") @Positive(message = "{validation.positive}") Long memberId,
            @LoginId Long loginMemberId) {

        ChannelInfo channelInfo = channelService.getChannel(memberId, loginMemberId);

        return ResponseEntity.ok(ApiSingleResponse.ok(channelInfo, "채널 조회가 완료되었습니다"));
    }


    // 채널 정보 수정
    @PatchMapping("/{member-id}")
    public ResponseEntity<Void> updateChannelInfo(
            @PathVariable("member-id") @Positive(message="{validation.positive}") Long memberId,
            @LoginId Long loginMemberId,
            @RequestBody @Valid ChannelUpdate updateChannel){

        channelService.updateChannelInfo(memberId, loginMemberId, updateChannel);

        return ResponseEntity.noContent().build();
    }

    // 구독 여부 업데이트
    @PatchMapping("/{member-id}/subscribe")
    public ResponseEntity<ApiSingleResponse<Boolean>> updateSubscribe(
            @PathVariable("member-id") @Positive(message = "{validation.positive}") Long memberId,
            @LoginId Long loginMemberId) {

        boolean isSubscribed = channelService.updateSubscribe(memberId, loginMemberId);

        return ResponseEntity.ok(ApiSingleResponse.ok(isSubscribed, "구독상태가 업데이트되었습니다."));
    }


    @GetMapping("/{member-id}/videos")
    public ResponseEntity<ApiPageResponse<ChannelVideoResponse>> getChannelVideos(
            @PathVariable("member-id") @Positive(message = "{validation.positive}") Long memberId,
            @RequestParam(value = "page", defaultValue = "1") @Positive(message = "{validation.positive}") int page,
            @RequestParam(value = "size", defaultValue = "16") @Positive(message = "{validation.positive}") int size,
            @RequestParam(value = "sort", defaultValue = "created-date") VideoSort sort,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "free", required = false) Boolean free,
            @RequestParam(value = "is-purchased", defaultValue = "true") boolean isPurchased,
            @LoginId Long loginMemberId
    ) {
        ChannelVideoGetServiceRequest request = ChannelVideoGetServiceRequest.builder()
                .memberId(memberId)
                .page(page - 1)
                .size(size)
                .sort(sort.getSort())
                .categoryName(category)
                .free(free)
                .isPurchased(isPurchased)
                .build();

        Page<ChannelVideoResponse> responses = channelService.getChannelVideos(loginMemberId, request);

        return ResponseEntity.ok(ApiPageResponse.ok(responses, "채널 비디오 목록 조회 성공"));
    }



    @PostMapping("/{member-id}/announcements")
    public ResponseEntity<ApiSingleResponse<Void>> createAnnouncement(
            @PathVariable("member-id") @Positive(message = "{validation.positive}") Long memberId,
            @RequestBody @Valid CreateAnnouncementApiRequest request,
            @LoginId Long loginMemberId
    ) {
        Long announcementId =
                announcementService.createAnnouncement(loginMemberId, request.toServiceRequest(memberId));

        URI uri = URI.create("/announcements/" + announcementId);

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{member-id}/announcements")
    public ResponseEntity<ApiPageResponse<AnnouncementResponse>> getAnnouncements(
            @PathVariable("member-id") @Positive(message = "{validation.positive}") Long memberId,
            @RequestParam(value = "page", defaultValue = "1") @Positive(message = "{validation.positive}") int page,
            @RequestParam(value = "size", defaultValue = "5") @Positive(message = "{validation.positive}") int size
    ) {

        Page<AnnouncementResponse> announcements = announcementService.getAnnouncements(memberId, page - 1, size);

        return ResponseEntity.ok(ApiPageResponse.ok(announcements, "공지사항 목록 조회 성공"));
    }

    @PostMapping("/{channel-id}/reports")
    public ResponseEntity<ApiSingleResponse<Boolean>> reportChannel(
            @PathVariable("channel-id") @Positive(message = "{validation.positive}") Long channelId,
            @RequestBody @Valid ReportCreateApiRequest request,
            @LoginId Long loginMemberId) {

        boolean result = channelService.reportChannel(loginMemberId, channelId, request.getReportContent());

        String message = result ? "채널 신고 성공" : "이미 신고한 채널입니다.";

        return ResponseEntity.ok(ApiSingleResponse.ok(result, message));
    }
}