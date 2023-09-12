package com.server.domain.reward.repository;

import com.server.domain.member.entity.Member;
import com.server.domain.reward.entity.Reward;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardRepository extends JpaRepository<Reward, Long>, RewardRepositoryCustom {

	Page<Reward> findRewardsByMember(Member member, Pageable pageable);

}