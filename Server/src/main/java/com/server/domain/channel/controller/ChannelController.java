package com.server.domain.channel.controller;

import com.server.domain.channel.service.ChannelService;
import com.server.domain.channel.service.dto.ChannelDto;
import com.server.domain.member.entity.Member;
import com.server.global.annotation.LoginId;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.module.s3.service.AwsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelService channelService;
    private final AwsService awsService;

    public ChannelController(ChannelService channelService, AwsService awsService){
        this.channelService = channelService;
        this.awsService = awsService;
    }

    @GetMapping("/{member-id}")
    public ResponseEntity<ApiSingleResponse<ChannelDto.ChannelInfo>> getChannelInfo(@PathVariable("member-id") @LoginId Long memberId){
        ChannelDto.ChannelInfo channelInfo = channelService.getChannelInfo(memberId);

        String imageUrl;

        try{
            imageUrl = awsService.getImageUrl(channelInfo.getImageUrl());
        } catch (Exception e){
            imageUrl = "...?";
        }

        channelInfo.setImageUrl(imageUrl);

        return ResponseEntity.ok(ApiSingleResponse.ok(channelInfo, "정보 불러오기가 완료되었습니다."));
    }

    @PatchMapping("/{member-id}")
    public ResponseEntity<ApiSingleResponse<Void>> updateChannelInfo(@PathVariable("member-id") @LoginId Long memberId,
                                                                     @RequestBody ChannelDto.UpdateInfo updateInfo,
                                                                     Member member){

        if (!memberId.equals(member.getMemberId())) {
            throw new MemberAccessDeniedException();
        }
        channelService.updateChannel(memberId, updateInfo, member);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{member-id}/subscribe")
    public ResponseEntity<Void> updateSubscribeOrUnsubscribe(@PathVariable("member-id") @LoginId Long memberId) {
        boolean isSubscribed = channelService.updateSubscribe(memberId);

        HttpStatus status = isSubscribed ? HttpStatus.OK : HttpStatus.NO_CONTENT;
        return ResponseEntity.status(status).build();
    }


    @GetMapping("/{member-id}/videos")
    public ResponseEntity<List<ChannelDto.ChannelVideoResponseDto>> getChannelVideos(
            @PathVariable("member-id") @LoginId Long memberId,
            @PathVariable("logged-in-member-id") Long loggedInMemberId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "category", required = false) String category) {

        if (!loggedInMemberId.equals(memberId)) {
            throw new MemberAccessDeniedException();
        }

        List<ChannelDto.ChannelVideoResponseDto> videoResponses = channelService.getChannelVideos(loggedInMemberId, memberId, page, sort);

        return ResponseEntity.ok(videoResponses);
    }
}