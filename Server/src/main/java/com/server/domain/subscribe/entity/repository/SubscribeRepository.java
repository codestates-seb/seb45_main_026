package com.server.domain.subscribe.entity.repository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.subscribe.entity.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    Subscribe findByMemberAndChannel(Member member, Channel channel);
}
