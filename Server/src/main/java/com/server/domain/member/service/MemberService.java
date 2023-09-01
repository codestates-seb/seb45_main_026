package com.server.domain.member.service;

import java.util.Optional;

import com.server.module.s3.service.dto.FileType;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.channel.service.ChannelService;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.member.service.dto.request.MemberServiceRequest;
import com.server.domain.member.service.dto.response.CartsResponse;
import com.server.domain.member.service.dto.response.OrdersResponse;
import com.server.domain.member.service.dto.response.PlaylistsResponse;
import com.server.domain.member.service.dto.response.ProfileResponse;
import com.server.domain.member.service.dto.response.RewardsResponse;
import com.server.domain.member.service.dto.response.SubscribesResponse;
import com.server.domain.member.service.dto.response.WatchsResponse;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberDuplicateException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberNotUpdatedException;
import com.server.global.exception.businessexception.memberexception.MemberPasswordException;
import com.server.module.email.service.MailService;
import com.server.module.s3.service.AwsService;

@Service
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;
	private final MailService mailService;
	private final ChannelService channelService;
	private final AwsService awsService;
	private final PasswordEncoder passwordEncoder;

	public MemberService(MemberRepository memberRepository, MailService mailService, ChannelService channelService,
						 AwsService awsService, PasswordEncoder passwordEncoder) {
		this.memberRepository = memberRepository;
		this.mailService = mailService;
		this.channelService = channelService;
		this.awsService = awsService;
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
		Member member = validateMember(loginId);

		return ProfileResponse.getMember(member,
				awsService.getFileUrl(
						member.getMemberId(),
						member.getImageFile(),
						FileType.PROFILE_IMAGE));
	}

	public Page<RewardsResponse> getRewards(Long loginId, int page) {
		return null;
	}

	public Page<SubscribesResponse> getSubscribes(Long loginId, int page) {
		return null;
	}

	// 좋아요 기능은 구현하지 않기로 함
	// public void getLikes(Long memberId, int page) {
	//
	// }

	public Page<CartsResponse> getCarts(Long loginId, int page) {
		return null;
	}

	public Page<OrdersResponse> getOrders(Long loginId, int page, int month) {
		return null;
	}

	public Page<PlaylistsResponse> getPlaylists(Long loginId, int page, String sort) {
		return null;
	}

	public Page<WatchsResponse> getWatchs(Long loginId, int page, int day) {

		return null;
	}

	@Transactional
	public void updatePassword(MemberServiceRequest.Password request, Long loginId) {
		Member member = validateMember(loginId);

		String password = member.getPassword();
		String newPassword = passwordEncoder.encode(request.getNewPassword());

		validatePassword(request.getPrevPassword(), password);

		member.setPassword(newPassword);
	}

	@Transactional
	public void updateNickname(MemberServiceRequest.Nickname request, Long loginId) {
		Member member = validateMember(loginId);

		member.setNickname(request.getNickname());
	}

	@Transactional
	public void updateImage(Long loginId) {
		Member member = validateMember(loginId);

		member.updateImageFile(member.getEmail());
	}

	@Transactional
	public void deleteMember(Long loginId) {
		Member member = validateMember(loginId);

		memberRepository.delete(member);
	}

	@Transactional
	public void deleteImage(Long loginId) {
		Member member = validateMember(loginId);

		awsService.deleteFile(loginId, member.getImageFile(), FileType.PROFILE_IMAGE);
		member.deleteImageFile();
	}

	public Member findMemberBy(Long id) {
		return memberRepository.findById(id).orElseThrow(
				MemberNotFoundException::new
		);
	}

	public void validatePassword(String password, String encodedPassword) {
		if(!passwordEncoder.matches(password, encodedPassword)) {
			throw new MemberPasswordException();
		}
	}

	public Member validateMember(Long loginId) {
		if (loginId < 1) {
			throw new MemberAccessDeniedException();
		}

		return memberRepository.findById(loginId).orElseThrow(
				MemberNotFoundException::new
		);
	}

	public void checkDuplicationEmail(String email) {
		memberRepository.findByEmail(email).ifPresent(member -> {
			throw new MemberDuplicateException();
		});
	}
}
