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

        Channel channel = existChannel(memberId);


        if (loginMemberId == null || loginMemberId.equals(-1L)) {


            return ChannelInfo.builder()
                    .memberId(-1L)
                    .channelName(channel.getChannelName())
                    .description(channel.getDescription())
                    .subscribers(channel.getSubscribers())
                    .isSubscribed(false)
                    .build();
        }
        else {

            boolean isSubscribed = isSubscribed(loginMemberId, memberId);

            return ChannelInfo.of(channel, isSubscribed);
        }
    }


    //채널정보 수정
    @Transactional
    public void updateChannelInfo(long ownerId, long loginMemberId, ChannelUpdate updateInfo) {


        //Member member = memberRepository.findById(loginMemberId).get().getChannel().getMember();



        if (loginMemberId != ownerId) {
            throw new MemberAccessDeniedException();
        }


        Channel channel = existChannel(ownerId);

        //Channel channel = existChannel(channelRepository.findByMember(loginMemberId).get().getChannelId());


        channel.updateChannel(updateInfo.getChannelName(), updateInfo.getDescription());

    }


    // 구독 여부 업데이트
    public boolean updateSubscribe(Long memberId, Long loginMemberId){

        if(loginMemberId == null || loginMemberId.equals(-1L)){
            throw new MemberAccessDeniedException();
        }


        if (isSubscribed(memberId, loginMemberId)) {

            unsubscribe(memberId, loginMemberId);

            return false;

        } else {

            subscribe(memberId, loginMemberId);

            return true;
        }
    }


    // 구독.
    private void subscribe(Long memberId, Long loginMemberId) {

          Member loginMember = memberRepository.findById(loginMemberId)
                  .orElseThrow(() -> new MemberNotFoundException()); //(이 부분을 빼면  .member(loginMember) 여기가 에러남)
//
        Channel channel = existChannel(memberId);


        channel.addSubscribers(1);

        Subscribe subscribe = Subscribe.builder()
                .member(loginMember)
                .channel(channel)
                .build();

        subscribeRepository.save(subscribe);
    }

    // 구독 취소
    private void unsubscribe(Long memberId, Long loginMemberId) { //이 부분 코드를 더 간결하게 못하겟음 수정할거 다 빼면 테스트코드 에러남

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