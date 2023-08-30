package com.server.domain.channel.service;

import com.server.domain.category.entity.Category;
import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.channel.service.dto.ChannelDto;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.subscribe.repository.SubscribeRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.domain.videoCategory.entity.VideoCategory;
import com.server.global.exception.businessexception.channelException.ChannelNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.PageInfo;
import com.server.module.s3.service.AwsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    public ChannelDto.ChannelInfo getChannelInfo(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        Channel channel = channelRepository.findByMember(member);
        if (channel == null) {
            throw new ChannelNotFoundException();
        }
        String imageUrl;
        try {
            imageUrl = awsService.getImageUrl(channel.getMember().getImageFile());
        } catch (Exception e) {
            imageUrl = "";
        }

        ChannelDto.ChannelInfo channelInfo = new ChannelDto.ChannelInfo();
        channelInfo.setMemberId(channel.getMember().getMemberId());
        channelInfo.setChannelName(channel.getChannelName());
        channelInfo.setSubscribers(channel.getSubscribers());
        channelInfo.setDescription(channel.getDescription());
        channelInfo.setCreatedDate(channel.getCreatedDate());
        channelInfo.setImageUrl(imageUrl);

        return channelInfo;
    }

    public void updateChannel(Long memberId, ChannelDto.UpdateInfo updateInfo, Member member) {

        Long loggedInMember = member.getMemberId();

        if(!loggedInMember.equals(memberId)){
            throw new MemberAccessDeniedException();
        }

        Channel channel = channelRepository.findByMember(member);

        if(!isChannelOwner(memberId, member) && !isAdmin(member)){
            throw new MemberAccessDeniedException();
        }

        if (updateInfo != null) {
            if (updateInfo.getChannelName() != null) {
                channel.setChannelName(updateInfo.getChannelName());
            }
            if (updateInfo.getDescription() != null) {
                channel.setDescription(updateInfo.getDescription());
            }

        }
    }

    public boolean updateSubscribe(Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        Channel channel = channelRepository.findByMember(member);

        if(channel == null){
            throw new ChannelNotFoundException();
        }

        if(isSubscribed(member, channel)){
            unsubscribe(member, channel);
            return false;
        } else
            subscribe(member, channel);
        return true;
    }


    //todo : 로그인한 사용자 정보를 받아서 memberId 와 비교하는 로직 필요
    public List<ChannelDto.ChannelVideoResponseDto> getChannelVideos(Long loggedInMemberId, Long memberId, int page, String sort) {
        if (!loggedInMemberId.equals(memberId)) {
            throw new MemberAccessDeniedException();
        }

        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        Channel channel = channelRepository.findByMember(member);

        PageRequest pageRequest;
        if ("createdAt".equals(sort)) {
            pageRequest = PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdDate");
        } else {
            pageRequest = PageRequest.of(page - 1, 10, Sort.Direction.DESC, sort);
        }

        Page<Video> videoPage = videoRepository.findByChannel(channel, pageRequest);

        List<ChannelDto.ChannelVideoResponseDto> videoResponses = new ArrayList<>();
        for (Video video : videoPage.getContent()) {
            List<String> categoryNames = new ArrayList<>();
            List<VideoCategory> categories = video.getVideoCategories();
//            for (Category category : categories) {
//                categoryNames.add(category.getCategoryName());
//            }


            String imageUrl;
            try {
                imageUrl = awsService.getImageUrl(channel.getMember().getImageFile());
            } catch (Exception e) {
                imageUrl = "";
            }

            ChannelDto.ChannelVideoResponseDto videoResponse = new ChannelDto.ChannelVideoResponseDto();
            videoResponse.setVideoId(video.getVideoId());
            videoResponse.setVideoName(video.getVideoName());
            videoResponse.setThumbnailUrl(imageUrl);
            videoResponse.setViews(video.getView());
            videoResponse.setPrice(video.getPrice());
            videoResponse.setCategories(categoryNames);
            videoResponse.setCreatedDate(video.getCreatedDate().toLocalDate());

            videoResponses.add(videoResponse);
        }

        return videoResponses;
    }



    public void createChannel(Member signMember) {
        Channel channel = Channel.createChannel(signMember.getNickname());

        channel.setMember(signMember);
        channelRepository.save(channel);
    }

    private boolean isChannelOwner(Long channelId, Member member){
        return member.getChannel() != null && member.getChannel().getChannelId().equals(channelId);
    }

    private boolean isAdmin(Member member){
        return member.getAuthority() == Authority.ROLE_ADMIN;
    }

    private boolean isSubscribed(Member member, Channel channel){
        Subscribe subscribe = subscribeRepository.findByMemberAndChannel(member, channel);
        return subscribe != null;
    }

    private void unsubscribe(Member member, Channel channel){
        Subscribe subscribe = subscribeRepository.findByMemberAndChannel(member, channel);
        if(subscribe != null){
            subscribeRepository.delete(subscribe);
        }
    }

    private void subscribe(Member member, Channel channel){
        Subscribe subscribe = new Subscribe(member, channel);

        subscribeRepository.save(subscribe);
    }
}
