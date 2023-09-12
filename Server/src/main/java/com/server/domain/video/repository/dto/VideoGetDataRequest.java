package com.server.domain.video.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;


@AllArgsConstructor
@Getter
@Builder
@ToString
public class VideoGetDataRequest {

    private Long loginMemberId;
    private Pageable pageable;
    private String categoryName;
    private String sort;
    private boolean subscribe;
    private Boolean free;
    private boolean isPurchased;
}
