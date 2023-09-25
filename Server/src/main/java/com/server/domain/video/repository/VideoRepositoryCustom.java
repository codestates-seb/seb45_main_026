package com.server.domain.video.repository;

import com.server.domain.report.entity.VideoReport;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.dto.request.ChannelVideoGetDataRequest;
import com.server.domain.video.repository.dto.request.VideoGetDataRequest;
import com.server.domain.report.repository.dto.response.VideoReportData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface VideoRepositoryCustom {

    Optional<Video> findVideoWithMember(Long videoId);

    Optional<Video> findVideoDetail(Long videoId);

    Optional<Video> findVideoDetailIncludeWithdrawal(Long videoId);

    Boolean isPurchased(Long memberId, Long videoId);

    Boolean isReplied(Long memberId, Long videoId);

    Optional<Video> findVideoByNameWithMember(Long memberId, String videoName);

    List<Long> findVideoIdInCart(Long memberId, List<Long> videoIds);

    Page<Video> findAllByCond(VideoGetDataRequest request);

    Page<Video> findAllByCond(String keyword, VideoGetDataRequest request);

    Page<Video> findChannelVideoByCond(ChannelVideoGetDataRequest request);

    Page<VideoReportData> findVideoReportDataByCond(Pageable pageable, String sort);

    Page<VideoReport> findReportsByVideoId(Long videoId, Pageable pageable);
}
