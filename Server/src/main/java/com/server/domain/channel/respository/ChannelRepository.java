package com.server.domain.channel.respository;

import com.server.domain.channel.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    Channel findByChannelId(Long memberId);
}
