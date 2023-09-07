package com.server.domain.channel.service.dto.request;

import com.server.domain.video.repository.dto.ChannelVideoGetDataRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;

@AllArgsConstructor
@Builder
@Getter
public class ChannelVideoGetServiceRequest {

    private Long memberId;
    private int page;
    private int size;
    private String categoryName;
    private String sort;
    private Boolean free;
    private boolean isPurchased;

    public ChannelVideoGetDataRequest toDataRequest() {
        return ChannelVideoGetDataRequest.builder()
                .memberId(memberId)
                .categoryName(categoryName)
                .pageable(PageRequest.of(page, size))
                .sort(sort)
                .free(free)
                .isPurchased(isPurchased)
                .build();
    }
}
