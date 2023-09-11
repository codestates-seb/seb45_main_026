package com.server.domain.member.repository;

import com.querydsl.core.Tuple;
import com.server.domain.cart.entity.Cart;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.dto.MemberVideoData;
import com.server.domain.order.entity.Order;
import com.server.domain.reward.entity.Reward;
import com.server.domain.video.entity.Video;
import com.server.domain.watch.entity.Watch;

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

    Page<Channel> findSubscribeWithChannelForMember(Long memberId, Pageable pageable);

    Page<Cart> findCartsOrderByCreatedDateForMember(Long memberId, Pageable pageable);

    Page<Order> findOrdersOrderByCreatedDateForMember(Long memberId, Pageable pageable, int month);

    Page<Watch> findWatchesForMember(Long memberId, Pageable pageable, int days);

    Page<Video>  findPlaylistsOrderBySort(Long memberId, Pageable pageable, String sort);

    Page<Tuple> findPlaylistGroupByChannelName(Long memberId, Pageable pageable);

    Page<Video> findPlaylistChannelDetails(Long loginId, Long memberId);
}
