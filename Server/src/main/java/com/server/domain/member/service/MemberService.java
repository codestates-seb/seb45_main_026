package com.server.domain.member.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
		validateLoginId(loginId);
		Member member = findMemberBy(loginId);

		return ProfileResponse.getMember(member, awsService.getImageUrl(member.getImageFile()));
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
		validateLoginId(loginId);



		return null;
	}

	@Transactional
	public void updatePassword(MemberServiceRequest.Password request, Long loginId) {
		validateLoginId(loginId);

		Member member = findMemberBy(loginId);

		String password = member.getPassword();
		String newPassword = request.getNewPassword();

		validatePassword(request.getPrevPassword(), password);

		if (newPassword == null || newPassword.equals(password)) {
			throw new MemberNotUpdatedException();
		}
		else {
			member.setPassword(passwordEncoder.encode(newPassword));
		}

		memberRepository.save(member);
	}

	@Transactional
	public void updateNickname(MemberServiceRequest.Nickname request, Long loginId) {
		validateLoginId(loginId);

		Member member = findMemberBy(loginId);

		if (request.getNickname() == null) {
			throw new MemberNotUpdatedException();
		} else {
			member.setNickname(request.getNickname());
		}

		memberRepository.save(member);
	}

	@Transactional
	public void updateImage(Long loginId) {
		validateLoginId(loginId);

		Member member = findMemberBy(loginId);

		if (member.getImageFile() == null) {
			Optional.ofNullable(member.getIdFromEmail()).ifPresent(
				member::setImageFile
			);

			memberRepository.save(member);
		}
	}

	@Transactional
	public void deleteMember(Long loginId) {
		validateLoginId(loginId);

		Member member = findMemberBy(loginId);

		memberRepository.delete(member);
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

	public void validateLoginId(Long loginId) {
		if (loginId < 1) {
			throw new MemberAccessDeniedException();
		}
	}

	public void checkDuplicationEmail(String email) {
		memberRepository.findByEmail(email).ifPresent(member -> {
			throw new MemberDuplicateException();
		});
	}
}
