package com.server.domain.video.service;

import com.server.domain.video.service.dto.request.VideoCreateServiceRequest;
import com.server.domain.video.service.dto.request.VideoCreateUrlServiceRequest;
import com.server.domain.video.service.dto.request.VideoUpdateServiceRequest;
import com.server.domain.video.service.dto.response.VideoCreateUrlResponse;
import com.server.domain.video.service.dto.response.VideoDetailResponse;
import com.server.domain.video.service.dto.response.VideoPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class VideoService {
    public Page<VideoPageResponse> getVideos(int page, int size, String sort, String category) {

        return null;
    }

    public VideoDetailResponse getVideo(Long loginMemberId, Long videoId) {
        return null;
    }

    public VideoCreateUrlResponse getVideoCreateUrl(Long loginMemberId, VideoCreateUrlServiceRequest request) {
        return null;
    }

    public Long createVideo(Long loginMemberId, VideoCreateServiceRequest request) {
        return null;
    }

    public void updateVideo(Long loginMemberId, VideoUpdateServiceRequest request) {
        return;
    }

    public Boolean changeCart(Long loginMemberId, Long videoId) {
        return null;
    }

    public void deleteVideo(Long loginMemberId, Long videoId) {
        return;
    }
}
