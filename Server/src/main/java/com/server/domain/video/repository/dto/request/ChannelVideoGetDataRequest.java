package com.server.domain.video.repository.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class ChannelVideoGetDataRequest {

    private Long memberId;
    private Long loginMemberId;
    private String categoryName;
    private Pageable pageable;
    private String sort;
    private Boolean free;
    private boolean isPurchased;
}
