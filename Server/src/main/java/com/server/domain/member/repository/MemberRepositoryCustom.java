package com.server.domain.member.repository;

import com.server.domain.cart.entity.Cart;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.dto.MemberSubscribesData;
import com.server.domain.member.repository.dto.MemberVideoData;
import com.server.domain.member.service.dto.response.CartsResponse;
import com.server.domain.member.service.dto.response.OrdersResponse;
import com.server.domain.member.service.dto.response.PlaylistsResponse;
import com.server.domain.member.service.dto.response.RewardsResponse;
import com.server.domain.member.service.dto.response.WatchsResponse;
import com.server.domain.order.entity.Order;
import com.server.domain.reward.entity.Reward;
import com.server.domain.video.entity.Video;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

    Boolean checkMemberPurchaseVideo(Long memberId, Long videoId);

    List<Boolean> checkMemberPurchaseVideos(Long memberId, List<Long> videoIds);

    List<Boolean> checkMemberSubscribeChannel(Long memberId, List<Long> ownerMemberIds);

    List<MemberVideoData> getMemberPurchaseVideo(Long memberId);

    Optional<Member> findByIdWithChannel(Long memberId);

    List<MemberSubscribesData> findSubscribeWithChannelForMember(Long memberId);

    List<Cart> findCartsOrderByCreatedDateForMember(Long memberId);

    List<Order> findOrdersOrderByCreatedDateForMember(Long memberId, int month);

    Page<WatchsResponse> findWatchesForMember(Long memberId, int days, Pageable pageable);

    Page<PlaylistsResponse> findPlaylistsOrderBySort(Long memberId, String sort, Pageable pageable);

    List<Reward> findRewardsByMemberId(Long memberId);
}
