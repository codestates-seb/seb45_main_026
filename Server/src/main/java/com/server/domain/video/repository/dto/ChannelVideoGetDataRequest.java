package com.server.domain.video.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@AllArgsConstructor
@Getter
@Builder
public class ChannelVideoGetDataRequest {

    private Long memberId;
    private String categoryName;
    private Pageable pageable;
    private String sort;
    private Boolean free;
    private boolean isPurchased;
}
