package com.server.domain.order.repository;

import com.server.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String>, OrderRepositoryCustom {
}