package com.server.domain.channel.controller;

import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.service.ChannelService;
import com.server.domain.channel.service.dto.ChannelDto;
import com.server.domain.member.entity.Member;
import com.server.global.annotation.LoginId;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.reponse.PageInfo;
import com.server.module.s3.service.AwsService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.util.List;


@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping("/{channelId}")
    public ResponseEntity<ChannelDto.ChannelInfo> getChannelInfo( //채널정보 조회
            @PathVariable Long channelId,
            @RequestParam(required = false) Long memberId) {

        ChannelDto.ChannelInfo channelInfo = channelService.getChannelInfo(memberId, channelId);

        return ResponseEntity.ok(channelInfo);
    }

    @PostMapping("/{memberId}")
    public ResponseEntity<Void> createChannel( //채널 생성 시, memberId를 받아서 채널 생성
            @PathVariable Long memberId,
            @RequestBody Member member) {

        if (!member.getMemberId().equals(memberId)) {
            throw new MemberAccessDeniedException();
        }

        channelService.createChannel(member);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PutMapping("/{memberId}/subscribe")
    public ResponseEntity<Void> updateSubscribe( //구독상태 업데이트
            @PathVariable Long memberId,
            @RequestParam Long loginMemberId) {

        boolean isSubscribed = channelService.updateSubscribe(memberId, loginMemberId);

        if (isSubscribed) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @GetMapping("/{memberId}/videos")
    public ResponseEntity<ApiPageResponse<ChannelDto.ChannelVideoResponseDto>> getChannelVideos(
            @PathVariable Long memberId,
            @RequestParam Long loggedInMemberId,
            @RequestParam int page,
            @RequestParam Sort sort) {

        ApiPageResponse<ChannelDto.ChannelVideoResponseDto> videoResponse =
                channelService.getChannelVideos(loggedInMemberId, memberId, page, sort);

        return ResponseEntity.ok(videoResponse);
    }
}