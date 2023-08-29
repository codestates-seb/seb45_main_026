package com.server.domain.announcement.repository;

import com.server.domain.announcement.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AnnouncementRepositoryCustom {

    Page<Announcement> findAnnouncementPageByMemberId(Long memberId, Pageable pageable);

    Optional<Announcement> findAnnouncementWithMember(Long announcementId);

}
