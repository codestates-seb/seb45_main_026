package com.server.domain.adjustment.repository;

import com.server.domain.adjustment.domain.Adjustment;
import com.server.domain.adjustment.repository.dto.AdjustmentData;
import com.server.domain.adjustment.repository.dto.VideoAdjustmentData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdjustmentRepositoryCustom {

    Page<AdjustmentData> findByPeriod(Long memberId, Pageable pageable, Integer month, Integer year, String sort);

    Integer calculateAmount(Long memberId, Integer month, Integer year);

    List<Adjustment> findMonthlyData(Long memberId, Integer year);

    List<VideoAdjustmentData> calculateVideo(Long memberId, Integer month, Integer year);
}
