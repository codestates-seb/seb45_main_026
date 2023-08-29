package com.server.domain.announcement.service;

import com.server.domain.announcement.entity.Announcement;
import com.server.domain.announcement.repository.AnnouncementRepository;
import com.server.domain.announcement.service.dto.request.AnnouncementCreateServiceRequest;
import com.server.domain.announcement.service.dto.request.AnnouncementUpdateServiceRequest;
import com.server.domain.announcement.service.dto.response.AnnouncementResponse;
import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.global.exception.announcementexception.AnnouncementNotFoundException;
import com.server.global.exception.businessexception.channelException.ChannelNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final ChannelRepository channelRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository, ChannelRepository channelRepository) {
        this.announcementRepository = announcementRepository;
        this.channelRepository = channelRepository;
    }

    public Page<AnnouncementResponse> getAnnouncements(Long memberId, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Announcement> announcements = announcementRepository.findAnnouncementPageByMemberId(memberId, pageRequest);

        return AnnouncementResponse.of(announcements);
    }

    public AnnouncementResponse getAnnouncement(Long announcementId) {

        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(AnnouncementNotFoundException::new);

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

        Announcement announcement = verifiedAnnouncement(loginMemberId, request.getAnnouncementId());

        announcement.updateAnnouncement(request.getContent());
    }

    @Transactional
    public void deleteAnnouncement(Long loginMemberId, Long announcementId) {

        //todo: 그냥 db 에 memberId 와 announcementId 로 delete 쿼리문만 날리고, 삭제 결과를 long 으로 받아서 처리해도 되지만,
        //todo: 이렇게 하면 삭제된 row 가 없어도 정상적으로 처리된다. 그래서 announcementId 가 db 에 없는건지, memberId 가 권한이 없는건지 구분이 안된다.
        //todo: 따라서 쿼리를 두번 날리더라도 그냥 announcement 를 찾아서 delete 하자. 예외처리 코드도 재사용 좀 하고.
        //todo: 쿼리문 2번은... 괜찮지 않을까. 삭제를 자주하는 것도 아니니까

        Announcement announcement = verifiedAnnouncement(loginMemberId, announcementId);

        announcementRepository.delete(announcement);
    }

    private void checkAuthority(Long loginMemberId, Long memberId) {
        if(!loginMemberId.equals(memberId)) throw new MemberAccessDeniedException();
    }

    private Channel verifiedChannel(Long memberId) {
        return channelRepository.findByMember(memberId)
                .orElseThrow(ChannelNotFoundException::new);
    }

    private Announcement verifiedAnnouncement(Long loginMemberId, Long announcementId) {

        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(AnnouncementNotFoundException::new);

        checkAuthority(loginMemberId, announcement.getChannel().getMember().getMemberId());

        return announcement;
    }
}
