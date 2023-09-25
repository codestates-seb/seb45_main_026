package com.server.domain.report.repository;

import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;

public interface ReportRepositoryCustom {

    boolean existsByMemberIdAndVideoId(Long memberId, Long videoId);

    boolean existsByMemberIdAndReplyId(Long memberId, Long replyId);

    boolean existsByMemberIdAndChannelId(Long memberId, Long channelId);

    boolean existsByMemberIdAndAnnouncementId(Long memberId, Long announcementId);
}
