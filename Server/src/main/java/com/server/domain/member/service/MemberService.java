package com.server.domain.member.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.server.domain.channel.service.ChannelService;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.member.service.dto.request.MemberServiceRequest;
import com.server.domain.member.service.dto.response.ProfileResponse;
import com.server.domain.member.service.dto.response.RewardsResponse;
import com.server.global.exception.businessexception.memberexception.MemberDuplicateException;
import com.server.module.email.service.MailService;

@Service
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;
	private final MailService mailService;
	private final ChannelService channelService;
	private final PasswordEncoder passwordEncoder;

	public MemberService(MemberRepository memberRepository, MailService mailService, ChannelService channelService,
		PasswordEncoder passwordEncoder) {
		this.memberRepository = memberRepository;
		this.mailService = mailService;
		this.channelService = channelService;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public void signUp(MemberServiceRequest.Create create) {
		checkDuplicationEmail(create.getEmail());
		mailService.checkEmailCertify(create.getEmail());

		Member member = Member.createMember(create.getEmail(), passwordEncoder.encode(create.getPassword()),
			create.getNickname());

		Member signMember = memberRepository.save(member);
		channelService.createChannel(signMember);
	}

	public ProfileResponse getMember(Long loginId) {
		return ProfileResponse.builder().build();
	}

	public Page<RewardsResponse> getRewards(Long memberId) {
		return null;
	}

	public void getSubscribes(Long memberId) {

	}

	public void getLikes(Long memberId, int page) {

	}

	public void getCarts(Long memberId, int page) {

	}

	public void getPlaylists(Long memberId, int page, String sort) {

	}

	public void getWatchs(Long memberId, int page, int day) {

	}

	public void updatePassword(MemberServiceRequest.Password request) {

	}

	public void updateNickname(MemberServiceRequest.Nickname request) {

	}

	public void updateImage(String imageName) {

	}

	public void deleteMember(Long memberId) {

	}

	public void checkDuplicationEmail(String email) {
		memberRepository.findByEmail(email).ifPresent(member -> {
			throw new MemberDuplicateException();
		});
	}
}
