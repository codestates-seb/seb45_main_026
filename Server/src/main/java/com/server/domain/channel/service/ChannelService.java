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

            Channel channel = existChannel(memberId); //조회하고자 하는 채널이 존재하는지 확인

            return ChannelInfo.builder()
                    .memberId(channel.getMember().getMemberId())
                    .channelName(channel.getChannelName())
                    .description(channel.getDescription())
                    .subscribers(channel.getSubscribers())
                    .isSubscribed(false)
                    .build();
        }
       else {

            Channel channel = existChannel(memberId); //조회하고자 하는 채널이 존재하는지 확인

            boolean isSubscribed = isSubscribed(loginMemberId, memberId);

            return ChannelInfo.of(channel, isSubscribed);
        }
    }


    //채널정보 수정
    @Transactional
    public void updateChannelInfo(long ownerId, long loginMemberId, ChannelUpdate updateInfo) { //...?????


        //Member member = memberRepository.findById(loginMemberId).get().getChannel().getMember(); //로그인한 사용자의 채널을 조회



        if (loginMemberId != ownerId) {
            throw new MemberAccessDeniedException(); //로그인한 사용자의 채널과 수정하고자 하는 채널이 다르면 MemberAccessDeniedException 발생
        }


        Channel channel = existChannel(channelRepository.findByMember(loginMemberId)
                .map(Channel::getChannelId)
                .orElseThrow(ChannelNotFoundException::new));

        //Channel channel = existChannel(channelRepository.findByMember(loginMemberId).get().getChannelId());


        channel.updateChannel(updateInfo.getChannelName(), updateInfo.getDescription());

    }


    // 구독 여부 업데이트
        public boolean updateSubscribe(Long memberId, Long loginMemberId){ // ..?? channelId, memberId ??

        if(memberId == null){
            throw new ChannelNotFoundException();
        }


        if (isSubscribed(memberId, loginMemberId)) { //구독중이면

            unsubscribe(memberId, loginMemberId); //구독 취소

            return false;

        } else {

            subscribe(memberId, loginMemberId);

            return true;
        }
    }


    // 구독.
    private void subscribe(Long memberId, Long loginMemberId) {

        Member loginMember = memberRepository.findById(loginMemberId).orElseThrow(() -> new MemberAccessDeniedException());

        Channel channel = existChannel(memberId);

        if (channel != null) {
            channel.addSubscribers(1);

        } else {
            throw new ChannelNotFoundException();
        }

        Subscribe subscribe = Subscribe.builder()
                .member(loginMember)
                .channel(channel)
                .build();

        subscribeRepository.save(subscribe);
    }

    // 구독 취소
    private void unsubscribe(Long memberId, Long loginMemberId) {

//        if (memberId == null) {
//            throw new ChannelNotFoundException();
//        }

        Channel findChannel = channelRepository.findById(memberId).orElseThrow(() -> new ChannelNotFoundException());

//        if (findChannel == null) {
//            throw new ChannelNotFoundException();
//        }

        Channel channel = findChannel.getMember().getChannel();

        Member loginMember = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new MemberNotFoundException());

//        if (isSubscribed(loginMemberId, memberId)) {
            channel.decreaseSubscribers(1);

            Optional<Subscribe> subscription = subscribeRepository.findByMemberAndChannel(loginMember, channel);

            subscription.ifPresent(subscribeRepository::delete);
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


    private Channel existChannel(Long loginMemberId) {

       //Member member = memberRepository.findById(loginMemberId).orElseThrow(MemberAccessDeniedException::new);

        return channelRepository.findByMember(loginMemberId).orElseThrow(ChannelNotFoundException::new);
    }
}