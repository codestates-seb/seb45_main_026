package com.server.domain.announcement.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class AnnouncementResponse {

    private Long announcementId;
    private String content;
    private LocalDateTime createdDate;
}
