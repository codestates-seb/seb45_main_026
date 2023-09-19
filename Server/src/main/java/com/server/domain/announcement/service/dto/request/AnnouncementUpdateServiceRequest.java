package com.server.domain.announcement.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
public class AnnouncementUpdateServiceRequest {

    private Long announcementId;
    private String content;
}
