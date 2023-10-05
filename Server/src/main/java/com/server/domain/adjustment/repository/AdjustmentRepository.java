package com.server.domain.adjustment.repository;

import com.server.domain.adjustment.domain.Adjustment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdjustmentRepository extends JpaRepository<Adjustment, Long>, AdjustmentRepositoryCustom {
}