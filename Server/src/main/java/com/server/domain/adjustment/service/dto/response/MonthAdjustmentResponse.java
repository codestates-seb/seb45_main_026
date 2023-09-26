package com.server.domain.adjustment.service.dto.response;

import com.server.domain.adjustment.domain.Adjustment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class MonthAdjustmentResponse {

    private Integer year;
    private Integer month;
    private Integer amount;

    public static MonthAdjustmentResponse of(Adjustment adjustment) {
        return MonthAdjustmentResponse.builder()
                .year(adjustment.getAdjustmentYear())
                .month(adjustment.getAdjustmentMonth())
                .amount(adjustment.getAmount())
                .build();
    }

    public static MonthAdjustmentResponse of(int year, int month, int amount) {
        return MonthAdjustmentResponse.builder()
                .year(year)
                .month(month)
                .amount(amount)
                .build();
    }

    public boolean isSameMonthAndYear(Integer year, Integer month) {
        return this.year.equals(year) && this.month.equals(month);
    }

}
