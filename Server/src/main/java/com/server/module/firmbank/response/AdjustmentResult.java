package com.server.module.firmbank.response;

import com.server.domain.adjustment.domain.AdjustmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AdjustmentResult {

    private AdjustmentStatus status;
    private String reason;
}
