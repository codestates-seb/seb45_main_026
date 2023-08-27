package com.server.domain.member.repository;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.dto.MemberVideoResponse;
import com.server.domain.order.service.dto.response.OrderVideoResponse;
import com.server.domain.video.entity.Video;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {

    Boolean checkMemberPurchaseVideo(Long memberId, Long videoId);

    List<MemberVideoResponse> getMemberPurchaseVideo(Long memberId);
}
