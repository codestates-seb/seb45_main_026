package com.server.domain.channel.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ChannelVideoGetServiceRequest {

    private Long memberId;
    private int page;
    private int size;
    private String categoryName;
    private String sort;
}
