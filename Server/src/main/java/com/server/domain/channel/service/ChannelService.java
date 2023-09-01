package com.server.domain.channel.service;

import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.channel.service.dto.ChannelInfo;
import com.server.domain.channel.service.dto.ChannelUpdate;
import com.server.domain.channel.service.dto.request.ChannelVideoGetServiceRequest;
import com.server.domain.channel.service.dto.response.ChannelVideoResponse;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.subscribe.repository.SubscribeRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.global.exception.businessexception.channelException.ChannelNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.module.s3.service.AwsService;
import com.server.module.s3.service.dto.FileType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final AwsService awsService;
    private final MemberRepository memberRepository;

    private final SubscribeRepository subscribeRepository;
    private final VideoRepository videoRepository;



    public ChannelService(ChannelRepository channelRepository,
                          AwsService awsService,
                          MemberRepository memberRepository,
                          SubscribeRepository subscribeRepository,
                          VideoRepository videoRepository) {

        this.channelRepository = channelRepository;
        this.awsService = awsService;
        this.memberRepository = memberRepository;
        this.subscribeRepository = subscribeRepository;
        this.videoRepository = videoRepository;
    }


    @Transactional(readOnly = true)
    public ChannelInfo getChannel(Long memberId, Long loginMemberId) {

        Channel channel = existChannel(memberId); //memberId로 수정했음, memberId 없으면 예외발생하는 로직 삭제함

        Boolean subscribed = isSubscribed(memberId, loginMemberId);



        return ChannelInfo.of(channel, subscribed, awsService.getFileUrl(memberId, channel.getMember().getImageFile(), FileType.PROFILE_IMAGE));
    }




    //채널정보 수정
    @Transactional
    public void updateChannelInfo(Long loginMemberId, long memberId, ChannelUpdate updateInfo){

        Channel channel = existChannel(memberId); //수정햇슴

        if(loginMemberId != memberId){
            throw new MemberAccessDeniedException();
        }

        channel.updateChannel(updateInfo.getChannelName(), updateInfo.getDescription());

        }





    // 구독 여부 업데이트
    public boolean updateSubscribe(Long loginMemberId, Long memberId) {


        if(isSubscribed(loginMemberId, memberId)){ //구독확인중복로직 제거, 만약 if가 true면 //여기는 true/false 판별하는 값이 잇어야해서..... 이건 안 뺏는데...

            unsubscribe(memberId, memberId); //구독해지

            return false;

        } else { isSubscribed(loginMemberId, memberId);

            return true;
        }
    }


    // 구독.
    private void subscribe(Long memberId, Long channelId) { //create할때만 save

        if(isSubscribed(memberId, channelId)){
            memberRepository.findById(memberId).get().getChannel().addSubscribers(1); //구독자 수 증가
        }

    }


    // 구독 취소
    private void unsubscribe(Long memberId, Long channelId) {

        if(isSubscribed(memberId, channelId)){
            memberRepository.findById(channelId).get().getChannel().addSubscribers(-1); //구독자 수 감소

       subscribeRepository.deleteSubscribeByChannelContains(channelId); //구독 취소


        } //중복은 일단 .. 구현하지않음
    }


    @Transactional(readOnly = true)
    public Page<ChannelVideoResponse> getChannelVideos(Long loginMemberId, ChannelVideoGetServiceRequest request) {

        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());

        Page<Video> videos = videoRepository.findChannelVideoByCategoryPaging(
                request.getMemberId(),
                request.getCategoryName(),
                pageRequest,
                request.getSort()
        );

        List<Boolean> isPurchaseInOrder = isPurchaseInOrder(loginMemberId, videos.getContent());

        List<String> thumbnailUrlsInOrder = getThumbnailUrlsInOrder(videos.getContent());

        return ChannelVideoResponse.of(videos, isPurchaseInOrder, thumbnailUrlsInOrder);
    }


    public void createChannel(Member signMember) {
        Channel channel = Channel.createChannel(signMember.getNickname());

        channel.setMember(signMember);
        channelRepository.save(channel);
    }



    private Boolean isSubscribed(Long loginMemberId, long memberId) { //구독중인지 확인, Channle로 받는 부분 수정 -> MemberId 받는 걸로

        if (loginMemberId == null) {
            return false;
        }


        return memberRepository.checkMemberSubscribeChannel(loginMemberId, List.of(memberId)).get(0);

    }



    private List<Boolean> isPurchaseInOrder(Long loginMemberId, List<Video> videos) {

        List<Long> videoIds = videos.stream()
                .map(Video::getVideoId)
                .collect(Collectors.toList());

        return memberRepository.checkMemberPurchaseVideos(loginMemberId, videoIds);
    }

    private List<String> getThumbnailUrlsInOrder(List<Video> videos) {

        return videos.stream()
                .map(video ->
                        getThumbnailUrl(video.getChannel().getMember().getMemberId(), video))
                .collect(Collectors.toList());
    }

    private String getThumbnailUrl(Long memberId, Video video) {
        return awsService.getFileUrl(memberId, video.getThumbnailFile(), FileType.THUMBNAIL);
    }



    private Channel existChannel(Long memberId) {
        return channelRepository.findByMember(memberId).orElseThrow(ChannelNotFoundException::new);
    }

}