package com.server.domain.channel.controller.dto.request;

import com.server.domain.announcement.service.dto.request.AnnouncementCreateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class CreateAnnouncementApiRequest {

    @NotBlank(message = "{validation.auth.code}")
    private String content;
    public AnnouncementCreateServiceRequest toServiceRequest(Long memberId) {
        return AnnouncementCreateServiceRequest.builder()
                .memberId(memberId)
                .content(content)
                .build();
    }
}
