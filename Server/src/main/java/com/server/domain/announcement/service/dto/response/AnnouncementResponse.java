package com.server.domain.announcement.service.dto.response;

import com.server.domain.announcement.entity.Announcement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class AnnouncementResponse {

    private Long announcementId;
    private String content;
    private LocalDateTime createdDate;

    public static Page<AnnouncementResponse> of(Page<Announcement> announcements) {
        return announcements.map(AnnouncementResponse::of);
    }

    public static AnnouncementResponse of(Announcement announcement) {
        return AnnouncementResponse.builder()
                .announcementId(announcement.getAnnouncementId())
                .content(announcement.getContent())
                .createdDate(announcement.getCreatedDate())
                .build();
    }
}
