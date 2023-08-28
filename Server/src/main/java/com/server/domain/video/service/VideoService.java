package com.server.domain.video.service;

import com.server.domain.member.repository.MemberRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.domain.video.service.dto.request.VideoCreateServiceRequest;
import com.server.domain.video.service.dto.request.VideoCreateUrlServiceRequest;
import com.server.domain.video.service.dto.request.VideoUpdateServiceRequest;
import com.server.domain.video.service.dto.response.VideoCreateUrlResponse;
import com.server.domain.video.service.dto.response.VideoDetailResponse;
import com.server.domain.video.service.dto.response.VideoPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
public class VideoService {

    private final VideoRepository videoRepository;
    private final MemberRepository memberRepository;

    public VideoService(VideoRepository videoRepository, MemberRepository memberRepository) {
        this.videoRepository = videoRepository;
        this.memberRepository = memberRepository;
    }

    public Page<VideoPageResponse> getVideos(Long loginMemberId, int page, int size, String sort, String category, boolean subscribe) {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Video> video = videoRepository.findAllByCategoryPaging(category, pageRequest, sort, loginMemberId, subscribe);

        List<Boolean> isPurchaseInOrder = isPurchaseInOrder(loginMemberId, video.getContent());

        List<Boolean> isSubscribeInOrder = isSubscribeInOrder(loginMemberId, video.getContent(), subscribe);

        return VideoPageResponse.of(video, isPurchaseInOrder, isSubscribeInOrder);
    }

    public VideoDetailResponse getVideo(Long loginMemberId, Long videoId) {
        return null;
    }

    @Transactional
    public VideoCreateUrlResponse getVideoCreateUrl(Long loginMemberId, VideoCreateUrlServiceRequest request) {
        return null;
    }

    @Transactional
    public Long createVideo(Long loginMemberId, VideoCreateServiceRequest request) {
        return null;
    }

    @Transactional
    public void updateVideo(Long loginMemberId, VideoUpdateServiceRequest request) {
        return;
    }

    @Transactional
    public Boolean changeCart(Long loginMemberId, Long videoId) {
        return null;
    }

    @Transactional
    public void deleteVideo(Long loginMemberId, Long videoId) {
        return;
    }

    private List<Boolean> isPurchaseInOrder(Long loginMemberId, List<Video> content) {

        List<Long> videoIds = content.stream()
                .map(Video::getVideoId)
                .collect(Collectors.toList());

        return memberRepository.checkMemberPurchaseVideos(loginMemberId, videoIds);
    }

    private List<Boolean> isSubscribeInOrder(Long loginMemberId, List<Video> content, boolean subscribe) {

        if (subscribe) {
            return IntStream.range(0, content.size())
                    .mapToObj(i -> true)
                    .collect(Collectors.toList());
        }

        List<Long> memberIds = content.stream()
                .map(video -> video.getChannel().getMember().getMemberId())
                .collect(Collectors.toList());

        return memberRepository.checkMemberSubscribeChannel(loginMemberId, memberIds);
    }
}
