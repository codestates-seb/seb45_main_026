package com.server.domain.announcement.service;

import com.server.domain.announcement.entity.Announcement;
import com.server.domain.announcement.repository.AnnouncementRepository;
import com.server.domain.announcement.service.dto.request.AnnouncementCreateServiceRequest;
import com.server.domain.announcement.service.dto.request.AnnouncementUpdateServiceRequest;
import com.server.domain.announcement.service.dto.response.AnnouncementResponse;
import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.report.service.ReportService;
import com.server.global.exception.businessexception.announcementexception.AnnouncementNotFoundException;
import com.server.global.exception.businessexception.channelException.ChannelNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;
    private final ReportService reportService;

    public AnnouncementService(AnnouncementRepository announcementRepository, ChannelRepository channelRepository, MemberRepository memberRepository, ReportService reportService) {
        this.announcementRepository = announcementRepository;
        this.channelRepository = channelRepository;
        this.memberRepository = memberRepository;
        this.reportService = reportService;
    }

    public Page<AnnouncementResponse> getAnnouncements(Long memberId, int page, int size) {

        verifiedChannel(memberId);

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Announcement> announcements = announcementRepository.findAnnouncementPageByMemberId(memberId, pageRequest);

        return AnnouncementResponse.of(announcements);
    }

    public AnnouncementResponse getAnnouncement(Long announcementId) {

        Announcement announcement = verifiedAnnouncement(announcementId);

        return AnnouncementResponse.of(announcement);
    }

    @Transactional
    public Long createAnnouncement(Long loginMemberId, AnnouncementCreateServiceRequest request) {

        checkAuthority(loginMemberId, request.getMemberId());

        Channel channel = verifiedChannel(request.getMemberId());

        Announcement announcement = Announcement.createAnnouncement(channel, request.getContent());

        return announcementRepository.save(announcement).getAnnouncementId();
    }

    @Transactional
    public void updateAnnouncement(Long loginMemberId, AnnouncementUpdateServiceRequest request) {

        Announcement announcement = verifiedAnnouncement(request.getAnnouncementId());

        checkAuthority(loginMemberId, announcement.getMemberId());

        announcement.updateAnnouncement(request.getContent());
    }

    @Transactional
    public void deleteAnnouncement(Long loginMemberId, Long announcementId) {

        Announcement announcement = verifiedAnnouncement(announcementId);

        checkAuthority(loginMemberId, announcement.getMemberId());

        announcementRepository.delete(announcement);
    }

    public boolean reportAnnouncement(Long loginMemberId, Long announcementId, String reportContent) {

        Announcement announcement = verifiedAnnouncement(announcementId);

        Member reporter = verifiedMember(loginMemberId);

        return reportService.reportAnnouncement(reporter, announcement, reportContent);
    }

    private Member verifiedMember(Long loginMemberId) {
        return memberRepository.findById(loginMemberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private void checkAuthority(Long loginMemberId, Long memberId) {
        if(!loginMemberId.equals(memberId)) throw new MemberAccessDeniedException();
    }

    private Channel verifiedChannel(Long memberId) {
        return channelRepository.findByMember(memberId)
                .orElseThrow(ChannelNotFoundException::new);
    }

    private Announcement verifiedAnnouncement(Long announcementId) {
        return announcementRepository.findAnnouncementWithMember(announcementId)
                .orElseThrow(AnnouncementNotFoundException::new);
    }
}
