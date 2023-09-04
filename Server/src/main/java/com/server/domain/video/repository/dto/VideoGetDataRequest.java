package com.server.domain.video.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;


@AllArgsConstructor
@Getter
@Builder
public class VideoGetDataRequest {

    private Long loginMemberId;
    private Pageable pageable;
    private String categoryName;
    private String sort;
    private boolean subscribe;
    private Boolean free;
}
