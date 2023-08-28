package com.server.domain.channel.respository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    Channel findByMember(Member member);
}
