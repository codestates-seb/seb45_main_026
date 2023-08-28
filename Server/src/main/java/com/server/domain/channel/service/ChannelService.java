package com.server.domain.channel.service;

import com.server.domain.category.entity.Category;
import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.channel.service.dto.ChannelDto;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.domain.videoCategory.entity.VideoCategory;
import com.server.global.exception.businessexception.channelException.ChannelNotFoundException;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.PageInfo;
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

    public ChannelService(ChannelRepository channelRepository, VideoRepository videoRepository) {
        this.channelRepository = channelRepository;
        this.videoRepository = videoRepository;
    }

    @Transactional(readOnly = true)
    public ChannelDto.ChannelInfo getChannelInfo(Long memberId) {
        Channel channel = channelRepository.findByChannelId(memberId);
        if (channel == null) {
            throw new ChannelNotFoundException();
        }

        //todo : 굳이 이렇게 따로 두지말고 ChannelInfo 파라미터로 바로 넣으면 됨
        int subscriberCount = channel.getSubscribers();
        boolean isSubscribed = channel.isSubscribed();

        //todo : imageUrl = awsService.getImageUrl(member.getImageUrl());

        return new ChannelDto.ChannelInfo(
                channel.getMember().getMemberId(),
                channel.getChannelName(),
                subscriberCount,
                isSubscribed,
                channel.getDescription(),
                channel.getImageUrl(),
                channel.getCreatedDate()
        );
    }

    public void updateChannel(Long memberId, ChannelDto.UpdateInfo updateInfo) {
        //todo : 로그인한 사용자와 memberId 가 같은지 확인하는 로직 필요
        Channel channel = channelRepository.findByChannelId(memberId);

        if (updateInfo != null) {
            if (updateInfo.getChannelName() != null) {
                channel.setChannelName(updateInfo.getChannelName());
            }

            if (updateInfo.getDescription() != null) {
                channel.setDescription(updateInfo.getDescription());
            }

            //todo : 아래 save 는 불필요 (dirty checking)
            channelRepository.save(channel);
        }
    }

    public boolean updateSubscribe(Long memberId) {
        //todo : member 와 channel 의 Subscribe 레코드가 있는지 확인하고 있으면 삭제, 없으면 생성... channel 의 isSubscribed 필드는 전혀 필요없음.
        //todo : 여기도 로그인한 사용자와 memberId 가 같은지 확인하는 로직 필요
        Channel channel = channelRepository.findByChannelId(memberId);
        channel.setSubscribed(!channel.isSubscribed());
        return channel.isSubscribed();
    }

    //todo : service 클래스에서 ApiPageResponse 를 사용하면 안됨. 따로 반환 클래스(dto) 를 만들 것
    public ApiPageResponse<ChannelDto.VideoResponse> getChannelVideos(Long memberId, int page, String sort){
        //todo : 로그인한 사용자 정보를 받아서 memberId 와 비교하는 로직 필요
        Channel channel = channelRepository.findByChannelId(memberId);
        PageRequest pageRequest;

        if ("createdAt".equals(sort)) {
            pageRequest = PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdDate");
        } else {
            pageRequest = PageRequest.of(page - 1, 10, Sort.Direction.DESC, sort);
        }

        Page<Video> videoPage = videoRepository.findByChannel(channel, pageRequest);


        List<ChannelDto.VideoResponse> videoResponses = new ArrayList<>();
        List<Video> videos = videoPage.getContent();
        for (Video video : videos) {
            List<String> categoryNames = new ArrayList<>();
            List<VideoCategory> categories = video.getVideoCategories();
//            for (Category category : categories) {
//                categoryNames.add(category.getCategoryName());
//            }

            ChannelDto.VideoResponse videoResponse = new ChannelDto.VideoResponse(
                    video.getVideoId(),
                    video.getVideoName(),
                    //todo : thumbnailUrl = awsService.getThumbnail(video.getThumbnailUrl());
                    video.getThumbnailFile(),
                    video.getView(),
                    video.getPrice(),
                    categoryNames,
                    video.getCreatedDate().toLocalDate()
            );
            videoResponses.add(videoResponse);
        }

        PageInfo pageInfo = PageInfo.of(videoPage);

        return new ApiPageResponse<>(videoResponses, pageInfo, 200, "OK", "채널목록이 조회되었습니다.");
    }

    public void createChannel(Member signMember) {
        Channel channel = Channel.createChannel(signMember.getNickname());

        channel.setMember(signMember);
        channelRepository.save(channel);
    }
}
