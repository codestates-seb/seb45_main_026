package com.server.domain.channel.respository;

import com.server.domain.channel.entity.Channel;

import java.util.Optional;

public interface ChannelRepositoryCustom {

    Optional<Channel> findByMember(Long memberId);

}
