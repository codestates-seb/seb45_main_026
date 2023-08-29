package com.server.domain.subscribe.entity.repository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.subscribe.entity.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
}
