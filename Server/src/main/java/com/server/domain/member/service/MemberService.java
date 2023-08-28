package com.server.domain.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.server.domain.channel.service.ChannelService;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.member.service.dto.request.MemberServiceRequest;
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

	public Member getMember(Long memberId, Long loginId) {
		return new Member();
	}

	public void getRewards(Long memberId) {
		// Member member = new Member();
		// // 클래스명 가져와서 리워드 타입으로 하면 될거 같기도...
		// System.out.println(member.getClass().getSimpleName().toUpperCase());
	}

	public void getSubscribes(Long memberId) {

	}

	public void getLikes(Long memberId, int page) {

	}

	public void getCarts(Long memberId, int page) {

	}

	public void getPays(Long memberId, int page, int month) {

	}

	public void getWatchs(Long memberId, int page, int day) {

	}

	public void updatePassword(MemberServiceRequest.Password request) {

	}

	public void updateNickname(MemberServiceRequest.Nickname request) {

	}

	public void updateImage(MultipartFile image) {

	}

	public void deleteMember(Long memberId) {

	}

	public void checkDuplicationEmail(String email) {
		memberRepository.findByEmail(email).ifPresent(member -> {
			throw new MemberDuplicateException();
		});
	}
}
