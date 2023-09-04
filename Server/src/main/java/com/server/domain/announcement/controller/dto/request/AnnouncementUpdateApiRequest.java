package com.server.domain.announcement.controller.dto.request;

import com.server.domain.announcement.service.dto.request.AnnouncementUpdateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class AnnouncementUpdateApiRequest {

    @NotBlank(message = "{validation.announcement.content}")
    private String content;

    public AnnouncementUpdateServiceRequest toServiceRequest(Long announcementId) {
        return AnnouncementUpdateServiceRequest.builder()
                .announcementId(announcementId)
                .content(this.content)
                .build();
    }
}
