package com.server.domain.channel.controller;

import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.service.ChannelService;
import com.server.domain.channel.service.dto.ChannelDto;
import com.server.domain.member.entity.Member;
import com.server.global.annotation.LoginId;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
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
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelService channelService;
    private final AwsService awsService;

    public ChannelController(ChannelService channelService, AwsService awsService){
        this.channelService = channelService;
        this.awsService = awsService;
    }

    @GetMapping("/{member-id}")
    public ResponseEntity<ApiSingleResponse<ChannelDto.ChannelInfo>> getChannelInfo(@PathVariable("member-id") Long memberId,
                                                                                    @Positive Long channelId){

        ChannelDto.ChannelInfo channelInfo = channelService.getChannelInfo(channelId);

        channelInfo.setImageUrl(awsService.getImageUrl("null"));

        return ResponseEntity.ok(ApiSingleResponse.ok(channelInfo, "조회가 완료되었습니다."));
    }

    @PatchMapping("/{member-id}") //반환타입이 맞추기가 어려움
    public ResponseEntity<ApiSingleResponse<Void>> updateChannelInfo(@PathVariable("member-id") @Positive Long memberId,
                                                                     @LoginId Long loginMemberId){
        if (!loginMemberId.equals(memberId)) {
            throw new MemberAccessDeniedException();
        }
        channelService.updateChannel(memberId, loginMemberId);

        return (ResponseEntity<ApiSingleResponse<Void>>) ResponseEntity.ok();
    }


    @PatchMapping("/{member-id}/subscribe")
    public ResponseEntity<Void> updateSubscribeOrUnsubscribe(@PathVariable("member-id") @Positive Long memberId,
                                                             @LoginId Long loginMemberId) {

        if(!loginMemberId.equals(memberId)){
            throw new MemberAccessDeniedException();
        }

        boolean isSubscribed = channelService.updateSubscribe(memberId, loginMemberId);

        HttpStatus status = isSubscribed ? HttpStatus.OK : HttpStatus.NO_CONTENT;

        return ResponseEntity.status(status).build();
    }


    @GetMapping("/{member-id}/videos") //service클래스에서 반환타입이 안 맞춰짐
    public ResponseEntity<List<ChannelDto.ChannelVideoResponseDto>> getChannelVideos(
            @PathVariable("member-id") @Positive Long memberId,
            @LoginId Long loginMemberId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "sort", defaultValue = "createdDate") Sort sort,
            @RequestParam(value = "category", required = false) String category) {

        if (!loginMemberId.equals(memberId)) {
            throw new MemberAccessDeniedException();
        }

        List<ChannelDto.ChannelVideoResponseDto> videoResponses = channelService.getChannelVideos(loginMemberId, memberId, page, sort);

        return ResponseEntity.ok(videoResponses);
    }
}