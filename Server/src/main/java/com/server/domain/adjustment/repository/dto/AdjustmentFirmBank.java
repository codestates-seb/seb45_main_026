package com.server.domain.adjustment.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentFirmBank {

    private int amount;
    private Long memberId;
}
