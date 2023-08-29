package com.server.domain.announcement.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class AnnouncementCreateServiceRequest {

    private Long announcementId;
    private String content;

}
