package com.server.domain.channel.controller;

import com.server.domain.channel.service.ChannelService;
import com.server.domain.channel.service.dto.ChannelDto;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelService channelService;
    //todo : dto 를 왜 component 로 ??????????????????
    private final ChannelDto channelDto;

    public ChannelController(ChannelService channelService, ChannelDto channelDto){
        this.channelService = channelService;
        this.channelDto = channelDto;
    }

    @GetMapping("/{member-id}") //채널 정보 조회
    public ResponseEntity<ApiSingleResponse<ChannelDto.ChannelInfo>> getChannelInfo(@PathVariable("member-id") Long memberId){
        ChannelDto.ChannelInfo channelInfo = channelService.getChannelInfo(memberId);
        return ResponseEntity.ok(ApiSingleResponse.ok(channelInfo, "정보 불러오기가 완료되었습니다."));
    }

    @PatchMapping("/{member-id}") //내 채널 정보 변경
    public ResponseEntity<ApiSingleResponse<Void>> updateChannelInfo(@PathVariable("member-id") Long memberId,
                                                                     @RequestBody ChannelDto.UpdateInfo updateInfo){

        //todo : @LoginId 어노테이션으로 로그인한 사용자의 id 를 가져와서 해당 채널을 수정할 수 있는지 권한 체크 (서비스 메서드 안에서)
        channelService.updateChannel(memberId, updateInfo);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{member-id}/subscribe") //구독상태 변경
    public ResponseEntity<ApiSingleResponse<ChannelDto.SubscribeStatus>> getSubscribeOrUnSubscribe(@PathVariable("member-id") Long memberId){
        boolean isSubscribed = channelService.updateSubscribe(memberId);

        //todo : @LoginId 어노테이션으로 로그인한 사용자의 id 를 가져와서 권한 체크
        ChannelDto.SubscribeStatus subscribe = new ChannelDto.SubscribeStatus(isSubscribed);

        return ResponseEntity.ok(ApiSingleResponse.ok(subscribe, "구독이 변경되었습니다."));
    }

    @GetMapping("/{member-id}/videos") //채널목록 조회
    public ResponseEntity<ApiPageResponse<ChannelDto.VideoResponse>> getChannelVideos(@PathVariable("member-id") Long memberId,
                                                                                      @RequestParam(value = "page", defaultValue = "1") int page,
                                                                                      @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
                                                                                      @RequestParam(value = "category", required = false) String category){

        //todo : @LoginId 어노테이션으로 로그인한 사용자가 해당 채널을 구독하고 있는지 체크
        //todo : channelService.getChannelVideos(LoginedMemberId, memberId, page, sort) 처럼 작성
        ApiPageResponse<ChannelDto.VideoResponse> videoPageResponse = channelService.getChannelVideos(memberId, page, sort);

        return ResponseEntity.ok(videoPageResponse);

    }

}
