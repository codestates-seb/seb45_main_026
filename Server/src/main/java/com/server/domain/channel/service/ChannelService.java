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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                    .memberId(channel.getMember().getMemberId())
                    .channelName(channel.getChannelName())
                    .description(channel.getDescription())
                    .subscribers(channel.getSubscribers())
                    .grade(channel.getMember().getGrade())
                    .isSubscribed(false)
                    .imageUrl(awsService.getFileUrl(
                              channel.getMember().getImageFile(),
                              FileType.PROFILE_IMAGE))
                    .createdDate(channel.getCreatedDate())
                    .build();
        }
        else {
            boolean isSubscribed = isSubscribed(loginMemberId, memberId);

            return ChannelInfo.of(channel, isSubscribed, awsService.getFileUrl(channel.getMember().getImageFile(), FileType.PROFILE_IMAGE));
        }
    }

    @Transactional
    public void updateChannelInfo(long ownerId, long loginMemberId, ChannelUpdate updateInfo) {

        if (loginMemberId != ownerId) {
            throw new MemberAccessDeniedException();
        }
        Channel channel = existChannel(ownerId);

        channel.updateChannel(updateInfo.getChannelName(), updateInfo.getDescription());
    }

    public boolean updateSubscribe(Long memberId, Long loginMemberId) {

        if (loginMemberId == null || loginMemberId.equals(-1L)) {
            throw new MemberAccessDeniedException();
        }
        boolean isSubscribed = isSubscribed(loginMemberId, memberId);

        if (!isSubscribed) {
            subscribe(memberId, loginMemberId);
            return true;
        } else {
            unsubscribe(memberId, loginMemberId);
            return false;
        }
    }

    private void subscribe(Long memberId, Long loginMemberId) {

        Member loginMember = memberRepository.findById(loginMemberId)
                .orElseThrow(MemberNotFoundException::new);

        Channel channel = existChannel(memberId);

        channel.addSubscriber();

        Subscribe subscribe = Subscribe.builder()
                .member(loginMember)
                .channel(channel)
                .build();

        subscribeRepository.save(subscribe);
    }

    private void unsubscribe(Long memberId, Long loginMemberId) {

        Channel channel = existChannel(memberId);

        Member loginMember = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new MemberNotFoundException());

        channel.decreaseSubscribers();

        subscribeRepository.findByMemberAndChannel(loginMember, channel)
                .ifPresent(subscribeRepository::delete);
    }


    @Transactional(readOnly = true)
    public Page<ChannelVideoResponse> getChannelVideos(Long loginMemberId, ChannelVideoGetServiceRequest request) {

        Member loginMember = verifiedMemberOrNull(loginMemberId);

        Page<Video> videos = videoRepository.findChannelVideoByCond(request.toDataRequest(loginMemberId));

        return ChannelVideoResponse.of(videos,
                isPurchase(loginMember, videos),
                getThumbnailUrls(videos),
                getVideoIdsInCart(loginMember, videos)
        );
    }

    private Member verifiedMemberOrNull(Long loginMemberId) {
        return memberRepository.findById(loginMemberId)
                .orElse(null);
    }
    public void createChannel(Member signMember) {
        Channel channel = Channel.createChannel(signMember.getNickname());

        channel.setMember(signMember);
        channelRepository.save(channel);
    }

    private Boolean isSubscribed(Long loginMemberId, long memberId) {

        return memberRepository.checkMemberSubscribeChannel(loginMemberId, List.of(memberId)).get(0);
    }

    private List<Boolean> isPurchase(Member loginMember, Page<Video> videos) {

        if(loginMember == null) {
            return createBooleans(videos.getContent().size(), false);
        }

        List<Long> videoIds = videos.getContent().stream()
                .map(Video::getVideoId)
                .collect(Collectors.toList());

        return memberRepository.checkMemberPurchaseVideos(loginMember.getMemberId(), videoIds);
    }

    private List<Boolean> createBooleans(int size, boolean value) {
        return IntStream.range(0, size)
                .mapToObj(i -> value)
                .collect(Collectors.toList());
    }

    private List<String> getThumbnailUrls(Page<Video> videos) {

        return videos.getContent().stream()
                .map(this::getThumbnailUrl)
                .collect(Collectors.toList());
    }

    private String getThumbnailUrl(Video video) {
        return awsService.getFileUrl(video.getThumbnailFile(), FileType.THUMBNAIL);
    }

    private List<Long> getVideoIdsInCart(Member loginMember, Page<Video> videos) {

        if(loginMember == null) {
            return Collections.emptyList();
        }

        List<Long> videoIds = videos.getContent().stream()
                .map(Video::getVideoId)
                .collect(Collectors.toList());

        return videoRepository.findVideoIdInCart(loginMember.getMemberId(), videoIds);
    }

    private Channel existChannel(Long memberId) {

        return channelRepository.findByMember(memberId).orElseThrow(ChannelNotFoundException::new);
    }
}