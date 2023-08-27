package com.server.domain.channel.service;

import com.server.domain.category.entity.Category;
import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.channel.service.dto.ChannelDto;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
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

        int subscriberCount = channel.getSubscribers();
        boolean isSubscribed = channel.isSubscribed();

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
        Channel channel = channelRepository.findByChannelId(memberId);

        if (updateInfo != null) {
            if (updateInfo.getChannelName() != null) {
                channel.setChannelName(updateInfo.getChannelName());
            }

            if (updateInfo.getDescription() != null) {
                channel.setDescription(updateInfo.getDescription());
            }

            channelRepository.save(channel);
        }
    }

    public boolean updateSubscribe(Long memberId) {
        Channel channel = channelRepository.findByChannelId(memberId);
        channel.setSubscribed(!channel.isSubscribed());
        return channel.isSubscribed();
    }
    public ApiPageResponse<ChannelDto.VideoResponse> getChannelVideos(Long memberId, int page, String sort){
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
            List<Category> categories = video.getVideoCategories();
            for (Category category : categories) {
                categoryNames.add(category.getCategoryName());
            }

            ChannelDto.VideoResponse videoResponse = new ChannelDto.VideoResponse(
                    video.getVideoId(),
                    video.getVideoName(),
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


}
