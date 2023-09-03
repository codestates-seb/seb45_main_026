package com.server.domain.channel.service;

import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.channel.service.dto.ChannelInfo;
import com.server.domain.channel.service.dto.ChannelUpdate;
import com.server.domain.channel.service.dto.request.ChannelVideoGetServiceRequest;
import com.server.domain.channel.service.dto.response.ChannelVideoResponse;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.subscribe.repository.SubscribeRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.global.exception.businessexception.channelException.ChannelNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.module.s3.service.AwsService;
import com.server.module.s3.service.dto.FileType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.lettuce.core.pubsub.PubSubOutput.Type.subscribe;

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

        if (loginMemberId == null) {
            throw new MemberNotFoundException();
        }

        Channel channel = existChannel(memberId);

        if (!loginMemberId.equals(memberId)) {
            throw new MemberAccessDeniedException();
        }

        //existChannel(memberId);

        Boolean subscribed = isSubscribed(memberId, loginMemberId);

        return ChannelInfo.of(channel, subscribed, awsService.getFileUrl(memberId, channel.getMember().getImageFile(), FileType.PROFILE_IMAGE));
    }




    //채널정보 수정
    @Transactional
    public void updateChannelInfo(Long loginMemberId, long memberId, ChannelUpdate updateInfo){

        if(loginMemberId != memberId){
            throw new MemberAccessDeniedException();
        }

        Channel channel = existChannel(memberId);

        channel.updateChannel(updateInfo.getChannelName(), updateInfo.getDescription());
    }




    // 구독 여부 업데이트
    public boolean updateSubscribe(Long loginMemberId, Long memberId) {

        if (!loginMemberId.equals(memberId)) {
            throw new MemberAccessDeniedException();
        }

        if(isSubscribed(loginMemberId, memberId)){

            return false;

        } else { subscribe(loginMemberId, memberId);

            return true;
        }
    }


    // 구독.
    private void subscribe(Long loginMemberId, Long memberId) {

        Member loginMember = memberRepository.findById(loginMemberId).orElseThrow(() -> new MemberNotFoundException());
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException());

        Channel channel = member.getChannel();

        if (channel != null && !isSubscribed(loginMemberId, memberId)) {
            channel.addSubscribers(1);

            Subscribe subscribe = Subscribe.builder()
                    .member(loginMember)
                    .channel(channel)
                    .build();

            subscribeRepository.save(subscribe);
        }
    }

    // 구독 취소
    private void unsubscribe(Long loginMemberId, Long memberId) {
        if (isSubscribed(loginMemberId, memberId)) {

            Channel channel = memberRepository.findById(memberId).get().getChannel();
            channel.decreaseSubscribers(1);

            Member loginMember = memberRepository.findById(loginMemberId).orElseThrow(() -> new MemberNotFoundException());

            Optional<Subscribe> subscription = subscribeRepository.findByMemberAndChannel(loginMember, channel);

            subscription.ifPresent(subscribeRepository::delete);
        }
    }


    @Transactional(readOnly = true)
    public Page<ChannelVideoResponse> getChannelVideos(Long loginMemberId, ChannelVideoGetServiceRequest request) {

        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());

        Page<Video> videos = videoRepository.findChannelVideoByCategoryPaging(
                request.getMemberId(),
                request.getCategoryName(),
                pageRequest,
                request.getSort(),
                request.getFree()
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



    private Boolean isSubscribed(Long loginMemberId, long memberId) {

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