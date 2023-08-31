package com.server.domain.channel.respository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Long>, ChannelRepositoryCustom {

    Optional<Channel> findByMember(Long memberId);

    Page<Channel> findAllBy(Pageable pageable, Long memberId, boolean subscribe);


}
