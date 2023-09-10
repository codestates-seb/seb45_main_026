package com.server.domain.reward.repository;

import com.server.domain.member.entity.Member;
import com.server.domain.reward.entity.NewReward;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NewRewardRepository extends JpaRepository<NewReward, Long>, NewRewardRepositoryCustom {

	Page<NewReward> findRewardsByMember(Member member, Pageable pageable);

}