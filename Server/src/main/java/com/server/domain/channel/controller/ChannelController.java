package com.server.domain.channel.controller;

import com.server.domain.announcement.service.AnnouncementService;
import com.server.domain.announcement.service.dto.response.AnnouncementResponse;
import com.server.domain.channel.service.ChannelService;
import com.server.domain.channel.service.dto.ChannelInfo;
import com.server.domain.channel.service.dto.ChannelResponse;
import com.server.domain.channel.service.dto.ChannelUpdate;
import com.server.domain.member.entity.Member;
import com.server.global.annotation.LoginId;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public ResponseEntity<ApiSingleResponse<ChannelInfo>> getChannel(@PathVariable("member-id") Long memberId,
                                                                     @LoginId Long loginMemberId) {

        ChannelInfo channelInfo = channelService.getChannel(memberId, loginMemberId);

        return ResponseEntity.ok(ApiSingleResponse.ok(channelInfo, "채널 조회가 완료되었습니다"));
    }


    // 채널 정보 수정
    @PatchMapping("/{member-id}")
    public ResponseEntity<Void> updateChannelInfo(@PathVariable("member-id") Long memberId,
                                                  @LoginId Long loginMemberId,
                                                  @RequestBody ChannelUpdate updateChannel){

        channelService.updateChannelInfo(memberId, loginMemberId, updateChannel);

        return ResponseEntity.noContent().build();
    }

    // 구독 여부 업데이트
    @PatchMapping("/{member-id}/subscribe")
    public ResponseEntity<ApiSingleResponse<Boolean>> updateSubscribe(@PathVariable("member-id") Long memberId,
                                                                      @LoginId Long loginMemberId){

        boolean isSubscribed = channelService.updateSubscribe(loginMemberId, memberId);

        return ResponseEntity.ok(ApiSingleResponse.ok(isSubscribed, "구독상태가 업데이트되었습니다."));
    }


    // 전체 채널 조회
//    @GetMapping
//    public ResponseEntity<ApiPageResponse<ChannelDto.ChannelResponseDto>> getChannels(Long memberId) {
//
//        Page<ChannelDto.ChannelResponseDto> channelInfos = channelService.getAllChannels(memberId);
//
//        return ResponseEntity.ok(ApiPageResponse.ok(channelInfos));
//    }



    @PostMapping("/{member-id}/announcements")
    public ResponseEntity<ApiSingleResponse<Void>> createAnnouncement(
            @PathVariable("member-id") Long memberId,
            @RequestBody @Valid ChannelResponse.CreateAnnouncementApiRequest request,
            @LoginId Long loginMemberId
    ) {
        Long announcementId =
                announcementService.createAnnouncement(loginMemberId, request.toServiceRequest(memberId));

        URI uri = URI.create("/announcements/" + announcementId);

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{member-id}/announcements")
    public ResponseEntity<ApiPageResponse<AnnouncementResponse>> getAnnouncements(
            @PathVariable("member-id") Long memberId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        Page<AnnouncementResponse> announcements = announcementService.getAnnouncements(memberId, page - 1, size);

        return ResponseEntity.ok(ApiPageResponse.ok(announcements, "공지사항 목록 조회 성공"));
    }
}