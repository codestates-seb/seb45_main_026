package com.server.domain.channel.service;

import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.channel.service.dto.ChannelDto;
import com.server.domain.channel.service.dto.request.ChannelVideoGetServiceRequest;
import com.server.domain.channel.service.dto.response.ChannelVideoResponse;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.member.repository.MemberRepositoryCustom;
import com.server.domain.subscribe.repository.SubscribeRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.global.exception.businessexception.channelException.ChannelNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.module.s3.service.AwsService;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final VideoRepository videoRepository;
    private final AwsService awsService;
    private final MemberRepositoryCustom memberRepositoryCustom;
    private final MemberRepository memberRepository;


    public ChannelService(ChannelRepository channelRepository,
                          VideoRepository videoRepository,
                          AwsService awsService,
                          SubscribeRepository subscribeRepository,
                          @Qualifier("memberRepositoryImpl") MemberRepositoryCustom memberRepositoryCustom,
                          MemberRepository memberRepository) {

        this.channelRepository = channelRepository;
        this.videoRepository = videoRepository;
        this.awsService = awsService;
        this.memberRepositoryCustom = memberRepositoryCustom;
        this.memberRepository = memberRepository;

    }


    @Transactional(readOnly = true)
    public ChannelDto.ChannelInfo getChannel(Long memberId, Long loginMemberId) {

        Channel channel = existChannel(loginMemberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Boolean subscribed = isSubscribed(loginMemberId, channel);

        return ChannelDto.ChannelInfo.of(channel,
                channel.getChannelName(),
                channel.getSubscribers(),
                subscribed,
                channel.getDescription(),
                channel.getMember().getImageFile(),
                channel.getCreatedDate());
    }



    private Boolean isSubscribed(Long loginMemberId, Channel channel) {
        if (loginMemberId == null) {
            return false;
        }

            return memberRepository.checkMemberSubscribeChannel(loginMemberId,
                    List.of(channel.getChannelId())).get(0); //최신순으로 정렬해서 첫번째 값만 가져온다.
        }



    //채널정보 수정
    @Transactional
    public void updateChannelInfo(Long loginMemberId, long memberId, ChannelDto.UpdateInfo updateInfo){

        Channel channel = existChannel(loginMemberId);

        if(loginMemberId != memberId){
            throw new MemberAccessDeniedException();
        }

        channel.updateChannel(updateInfo.getChannelName(), updateInfo.getDescription());

        }





    // 구독 여부 업데이트
    public boolean updateSubscribe(Long loginMemberId, Channel channel) {

        // 이미 구독 중인지 여부 확인
        boolean isSubscribed = isSubscribed(loginMemberId, channel);

        if (isSubscribed) { //구독중이면 해지

            unsubscribe(loginMemberId, memberRepositoryCustom.checkMemberSubscribeChannel(loginMemberId, List.of(channel.getChannelId())).get(0)); //구독취소

            return false;

        } else { //구독중이 아니면 구독

            return true;
        }
    }


    // 구독.
    private void subscribe(Long memberId, Long channelId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(ChannelNotFoundException::new);

      List<Boolean> subscribeList =  memberRepositoryCustom.checkMemberSubscribeChannel(memberId, List.of(channelId));

      if(!subscribeList.contains(true)) //내 목록에 현재 채널이 없으면 구독
          channel.addSubscribers(1); //구독자 수 증가
         channelRepository.save(channel);
    }


    // 구독 취소
    private void unsubscribe(Long memberId, Boolean channelId) {

        Member member = memberRepository.findById(memberId) //일단 member를 찾는다
                .orElseThrow(MemberNotFoundException::new);

        Channel channel = channelRepository.findById(memberId) //채널을 찾는다
                .orElseThrow(ChannelNotFoundException::new);

        List<Boolean> subscribeList =  memberRepositoryCustom.checkMemberSubscribeChannel(memberId, List.of(memberId));

        if(subscribeList.contains(true)){ //이미 구독중이면
        channel.decreaseSubscribers(1); //구독자 수 감소
        channelRepository.save(channel);

    } //근데 중복 처리도 해야할 것 같은.. 구독중이 아닌데 구독취소를 누르면 어떻게 되는지..??
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

    private Channel existChannel(Long memberId) {
        return channelRepository.findByMember(memberId)
                .orElseThrow(ChannelNotFoundException::new);
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
        return awsService.getThumbnailUrl(memberId, video.getThumbnailFile());
    }



}