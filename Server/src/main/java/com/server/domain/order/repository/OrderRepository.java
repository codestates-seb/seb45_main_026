package com.server.domain.order.repository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String>, OrderRepositoryCustom {

	@Modifying
	@Query("UPDATE orders o SET o.member = null WHERE o.member = :member")
	void disconnectOrdersFromMember(@Param("member") Member member);
}