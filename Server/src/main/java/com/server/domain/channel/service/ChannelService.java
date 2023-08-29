package com.server.domain.channel.service;

import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.channel.service.dto.ChannelDto;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.subscribe.entity.repository.SubscribeRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.global.exception.businessexception.channelException.ChannelNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.module.s3.service.AwsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final VideoRepository videoRepository;
    private final AwsService awsService;
    private final MemberRepository memberRepository;
    private final SubscribeRepository subscribeRepository;


    public ChannelService(ChannelRepository channelRepository,
                          VideoRepository videoRepository,
                          AwsService awsService,
                          MemberRepository memberRepository,
                          SubscribeRepository subscribeRepository ) {

        this.channelRepository = channelRepository;
        this.videoRepository = videoRepository;
        this.awsService = awsService;
        this.memberRepository = memberRepository;
        this.subscribeRepository = subscribeRepository;

    }

    @Transactional(readOnly = true)
    public ChannelDto.ChannelInfo getChannelInfo(Long channelId) {

        Member member = new Member();

        Channel channel = channelRepository.findByChannelId(channelId);

        if (channel == null) {
            throw new ChannelNotFoundException();
        }

        ChannelDto.ChannelInfo channelInfo = new ChannelDto.ChannelInfo();
        channelInfo.setMemberId(channel.getMember().getMemberId());
        channelInfo.setChannelName(channel.getChannelName());
        channelInfo.setSubscribers(channel.getSubscribers());
        channelInfo.setDescription(channel.getDescription());
        channelInfo.setCreatedDate(channel.getCreatedDate());
        channelInfo.setImageUrl(member.getImageFile());

        return channelInfo;
    }

    public void updateChannel(Long memberId,
                              Long loginMemberId) {

        Member member = new Member();

        Channel channel = member.getChannel();

        ChannelDto.UpdateInfo updateInfo = new ChannelDto.UpdateInfo();

        if (!loginMemberId.equals(memberId)) { //로그인했는지 여부
            throw new MemberAccessDeniedException();
        }


        if (!isChannelOwner(channel.getChannelId(), channel) && !isLoggedInMember(loginMemberId, memberId)) { //로그인도 안했고 채널주인도 아니면
            throw new MemberAccessDeniedException();
        }


        Optional.ofNullable(updateInfo.getChannelName())
                .ifPresent(channel::setChannelName);

        Optional.ofNullable(updateInfo.getDescription())
                .ifPresent(channel::setDescription);

    }

    public boolean updateSubscribe(Long memberId, Long loginMemberId) {

        Member member =memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Channel channel = member.getChannel();

        if(!memberId.equals(loginMemberId)){
            throw new MemberAccessDeniedException();
        }

        if(channel == null){
            throw new ChannelNotFoundException();
        }

        if(isSubscribed(memberId, channel)){
            unsubscribe(memberId, channel);
            return false;
        } else
            subscribe(member, channel);
        return true;
    }


    public List<ChannelDto.ChannelVideoResponseDto> getChannelVideos( //apiReponse로 반환타입 맞추기가 어려움..
            Long loggedInMemberId,
            Long memberId,
            int page,
            Sort sort) {

        if (!loggedInMemberId.equals(memberId)) {
            throw new MemberAccessDeniedException();
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Channel channel = member.getChannel();
        if (channel == null) {
            throw new ChannelNotFoundException();
        }

        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.Direction.DESC, String.valueOf(sort));
        Page<Video> videoPage = videoRepository.findByChannel(channel, pageRequest);

        List<ChannelDto.ChannelVideoResponseDto> videoResponses = new ArrayList<>();
        for (Video video : videoPage.getContent()) {
            String imageUrl = awsService.getImageUrl(member.getImageFile());

            ChannelDto.ChannelVideoResponseDto videoResponse = getVideoResponseDto(video, imageUrl);
            videoResponses.add(videoResponse);
        }

        return videoResponses;
    }

    private ChannelDto.ChannelVideoResponseDto getVideoResponseDto(Video video, String imageUrl) {
        ChannelDto.ChannelVideoResponseDto videoResponse = new ChannelDto.ChannelVideoResponseDto();
        videoResponse.setVideoId(video.getVideoId());
        videoResponse.setVideoName(video.getVideoName());
        videoResponse.setThumbnailUrl(imageUrl);
        videoResponse.setViews(video.getView());
        videoResponse.setPrice(video.getPrice());
        videoResponse.setCategories(new ArrayList<>()); // Assuming categoryNames are not used
        videoResponse.setCreatedDate(video.getCreatedDate().toLocalDate());
        return videoResponse;
    }

    private static ChannelDto.ChannelVideoResponseDto getVideoResponseDto(Video video, String imageUrl, List<ChannelDto.Category> categoryNames) {
        ChannelDto.ChannelVideoResponseDto videoResponse = new ChannelDto.ChannelVideoResponseDto();
        videoResponse.setVideoId(video.getVideoId());
        videoResponse.setVideoName(video.getVideoName());
        videoResponse.setThumbnailUrl(imageUrl);
        videoResponse.setViews(video.getView());
        videoResponse.setPrice(video.getPrice());
        videoResponse.setCategories(categoryNames);
        videoResponse.setCreatedDate(video.getCreatedDate().toLocalDate());
        return videoResponse;
    }


    public void createChannel(Member signMember) {
        Channel channel = Channel.createChannel(signMember.getNickname());

        channel.setMember(signMember);
        channelRepository.save(channel);
    }

    boolean isLoggedInMember(Long loginMemberId, Long adminId) {
        Member adminMember = memberRepository.findById(adminId).orElse(null);
        if (adminMember != null) {
            return loginMemberId.equals(adminMember.getMemberId());
        }
        return false;
    }

    private boolean isChannelOwner(Long memberId, Channel channel) {
        return channel.getMember().getMemberId().equals(memberId);
    }



    private boolean isSubscribed(Long memberId, Channel channel){
        Subscribe subscribe = subscribeRepository.findSubscribeByChannel(channel);
        return subscribe != null;
    }

    private void unsubscribe(Long memberId, Channel channel){
        Subscribe subscribe = subscribeRepository.findSubscribeByChannel(channel);
        if(subscribe != null){
            subscribeRepository.delete(subscribe);
        }
    }

    private void subscribe(Member member, Channel channel){
        Subscribe subscribe = new Subscribe(member, channel);

        subscribeRepository.save(subscribe);
    }


}
