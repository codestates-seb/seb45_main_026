package com.server.domain.announcement.service;

import com.server.domain.announcement.service.dto.request.AnnouncementCreateServiceRequest;
import com.server.domain.announcement.service.dto.request.AnnouncementUpdateServiceRequest;
import com.server.domain.announcement.service.dto.response.AnnouncementResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AnnouncementService {

    public Page<AnnouncementResponse> getAnnouncements(int page, int size) {

        return null;
    }

    public AnnouncementResponse getAnnouncement(Long announcementId) {

        return null;
    }

    @Transactional
    public Long createAnnouncement(Long loginMemberId, AnnouncementCreateServiceRequest request) {

        return null;
    }

    @Transactional
    public void updateAnnouncement(Long loginMemberId, AnnouncementUpdateServiceRequest request) {

    }

    @Transactional
    public void deleteAnnouncement(Long loginMemberId, Long announcementId) {

    }
}
