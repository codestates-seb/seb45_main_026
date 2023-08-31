package com.server.domain.member.repository;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.dto.MemberVideoData;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {

    Boolean checkMemberPurchaseVideo(Long memberId, Long videoId);

    List<Boolean> checkMemberPurchaseVideos(Long memberId, List<Long> videoIds);

    List<Boolean> checkMemberSubscribeChannel(Long memberId, List<Long> ownerMemberIds);

    List<MemberVideoData> getMemberPurchaseVideo(Long memberId);

    Optional<Member> findByIdWithChannel(Long memberId);
}
