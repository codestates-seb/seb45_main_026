package com.server.domain.channel.service.dto;

import com.server.domain.announcement.service.dto.request.AnnouncementCreateServiceRequest;
import com.server.domain.category.entity.Category;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

 @AllArgsConstructor
    @Builder
    @Getter
    public class ChannelResponse {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateAnnouncementApiRequest {
        private String content;

        public AnnouncementCreateServiceRequest toServiceRequest(Long memberId) {
            return AnnouncementCreateServiceRequest.builder()
                    .memberId(memberId)
                    .content(content)
                    .build();
        }
    }
}

