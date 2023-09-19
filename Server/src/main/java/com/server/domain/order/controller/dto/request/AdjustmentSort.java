package com.server.domain.order.controller.dto.request;

import com.server.domain.video.controller.dto.request.VideoSort;
import com.server.global.entity.BaseEnum;
import lombok.AllArgsConstructor;
import org.springframework.beans.TypeMismatchException;

@AllArgsConstructor
public enum AdjustmentSort implements BaseEnum {

    TOTAL_SALE_AMOUNT("총 판매 금액 순", "total-sale-amount", "totalSaleAmount"),
    REFUND_AMOUNT("총 환불 순", "refund-amount", "refundAmount"),
    CREATED_DATE("비디오 생성일 순", "video-created-date", "createdDate"),
    ;

    private final String description;
    private final String url;
    private final String sort;

    @Override
    public String getName() {
        return this.url;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public String getSort() {
        return this.sort;
    }

    public static AdjustmentSort fromUrl(String url) {
        for (AdjustmentSort adjustmentSort : values()) {
            if (adjustmentSort.url.equals(url)) {
                return adjustmentSort;
            }
        }
        throw new TypeMismatchException(url, AdjustmentSort.class);
    }
}
