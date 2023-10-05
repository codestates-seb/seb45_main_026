package com.server.domain.adjustment.service.dto.response;

import com.server.domain.adjustment.repository.dto.VideoAdjustmentData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class VideoAdjustmentResponse {

    private Long videoId;
    private String videoName;
    private Integer amount;
    private Float portion;

    public static VideoAdjustmentResponse of(VideoAdjustmentData data, int total) {

        float portion = ((float) (data.getAmount())) / total;

        return VideoAdjustmentResponse.builder()
                .videoId(data.getVideoId())
                .videoName(data.getVideoName())
                .amount(data.getAmount())
                .portion(portion)
                .build();
    }
}
