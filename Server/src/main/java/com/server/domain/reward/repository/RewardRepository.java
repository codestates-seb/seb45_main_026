package com.server.domain.reward.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.domain.reward.entity.Reward;

public interface RewardRepository extends JpaRepository<Reward, Long> {

}
