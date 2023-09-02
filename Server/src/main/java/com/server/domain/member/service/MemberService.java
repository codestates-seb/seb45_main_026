package com.server.domain.member.service;

import java.util.List;
import java.util.stream.Collectors;

import com.server.domain.cart.entity.Cart;
import com.server.domain.channel.service.ChannelService;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.member.repository.dto.MemberSubscribesData;
import com.server.domain.member.service.dto.request.MemberServiceRequest;
import com.server.domain.member.service.dto.response.*;
import com.server.domain.order.entity.Order;
import com.server.domain.reward.entity.Reward;
import com.server.domain.video.entity.Video;
import com.server.domain.watch.entity.Watch;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberDuplicateException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberPasswordException;
import com.server.global.exception.businessexception.s3exception.S3FileNotVaildException;
import com.server.module.email.service.MailService;
import com.server.module.s3.service.AwsService;
import com.server.module.s3.service.AwsServiceImpl;
import com.server.module.s3.service.dto.FileType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

		if (member.getImageFile() == null) {
			return ProfileResponse.getMember(member,
				"프로필 이미지 미등록");
		}

		return ProfileResponse.getMember(member, getFileUrl(member));
	}

	public Page<RewardsResponse> getRewards(Long loginId, int page, int size) {
		Member member = validateMember(loginId);

		List<Reward> rewards = memberRepository.findRewardsByMemberId(member.getMemberId());

		List<RewardsResponse> rewardsResponses = RewardsResponse.convert(rewards);

		return new PageImpl<>(rewardsResponses, PageRequest.of(page - 1, size), rewardsResponses.size());
	}

	public Page<SubscribesResponse> getSubscribes(Long loginId, int page, int size) {
		Member member = validateMember(loginId);

		List<MemberSubscribesData> memberSubscribesData = memberRepository.findSubscribeWithChannelForMember(member.getMemberId());

		setProfileImage(memberSubscribesData);

		List<SubscribesResponse> result = SubscribesResponse.convertSubscribesResponse(memberSubscribesData);


		return new PageImpl<>(result, PageRequest.of(page - 1, size), result.size());
	}

	public Page<CartsResponse> getCarts(Long loginId, int page, int size) {
		Member member = validateMember(loginId);

		List<Cart> carts = memberRepository.findCartsOrderByCreatedDateForMember(member.getMemberId());

		List<CartsResponse> result = CartsResponse.convert(carts);

		setImageUrl(result);

		return new PageImpl<>(result, PageRequest.of(page - 1, size), result.size());
	}

	public Page<OrdersResponse> getOrders(Long loginId, int page, int size, int month) {
		Member member = validateMember(loginId);

		List<Order> orders = memberRepository.findOrdersOrderByCreatedDateForMember(member.getMemberId(), month);

		List<OrdersResponse> responses = convertOrdersToOrdersResponses(orders);

		return new PageImpl<>(responses, PageRequest.of(page - 1, size), responses.size());
	}

	public Page<PlaylistsResponse> getPlaylists(Long loginId, int page, int size, String sort) {
		Member member = validateMember(loginId);

		List<Video> videos = memberRepository.findPlaylistsOrderBySort(member.getMemberId(), sort);

		List<PlaylistsResponse> responses = convertVideosToPlaylistsResponses(videos);

		return new PageImpl<>(responses, PageRequest.of(page - 1, size), responses.size());
	}

	public Page<WatchsResponse> getWatchs(Long loginId, int page, int size, int day) {
		Member member = validateMember(loginId);

		List<Watch> watches = memberRepository.findWatchesForMember(member.getMemberId(), day);

		List<WatchsResponse> responses = convertWatchToWatchResponses(watches);

		return new PageImpl<>(responses, PageRequest.of(page - 1, size), responses.size());
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

	private void setImageUrl(List<CartsResponse> result) {
		for (CartsResponse c : result) {
			c.setThumbnailUrl(
				getThumbnailUrl(
					c.getChannel().getMemberId(), c.getThumbnailUrl())
			);
			c.getChannel().setImageUrl(
				awsService.getFileUrl(
					c.getChannel().getMemberId(),
					c.getChannel().getImageUrl(),
					FileType.PROFILE_IMAGE)
			);
		}
	}

	private void setProfileImage(List<MemberSubscribesData> memberSubscribesData) {
		for (MemberSubscribesData response : memberSubscribesData) {
			String imageUrl = awsService.getFileUrl(
				response.getMemberId(),
				response.getImageUrl(),
				FileType.PROFILE_IMAGE);
			response.setImageUrl(imageUrl);
		}
	}

	private String getFileUrl(Member member) {
		return awsService.getFileUrl(
			member.getMemberId(),
			member.getImageFile(),
			FileType.PROFILE_IMAGE);
	}

	private String getThumbnailUrl(Long memberId, String thumbnailFile) {
		return awsService.getFileUrl(memberId, thumbnailFile, FileType.THUMBNAIL);
	}

	private List<OrdersResponse> convertOrdersToOrdersResponses(List<Order> orders) {
		return orders.stream()
			.map(order -> OrdersResponse.builder()
				.orderId(order.getOrderId())
				.reward(order.getReward())
				.orderCount(order.getOrderVideos().size())
				.orderStatus(order.getOrderStatus())
				.createdDate(order.getCreatedDate())
				.orderVideos(order.getOrderVideos().stream()
					.map(orderVideo -> OrdersResponse.OrderVideo.builder()
						.videoId(orderVideo.getVideo().getVideoId())
						.videoName(orderVideo.getVideo().getVideoName())
						.thumbnailFile(getThumbnailUrl(orderVideo.getVideo().getChannel().getMember().getMemberId(), orderVideo.getVideo().getThumbnailFile()))
						.channelName(orderVideo.getVideo().getChannel().getChannelName())
						.price(orderVideo.getVideo().getPrice())
						.build())
					.collect(Collectors.toList()))
				.build())
			.collect(Collectors.toList());
	}

	private List<PlaylistsResponse> convertVideosToPlaylistsResponses(List<Video> videos) {
		return videos.stream()
			.map(video -> PlaylistsResponse.builder()
				.videoId(video.getVideoId())
				.videoName(video.getVideoName())
				.thumbnailFile(
					getThumbnailUrl(video.getChannel().getMember().getMemberId(),
					video.getThumbnailFile())
				)
				.star(video.getStar())
				.modifiedDate(video.getModifiedDate())
				.channel(
					PlaylistsResponse.Channel.builder()
						.memberId(video.getChannel().getMember().getMemberId())
						.channelName(video.getChannel().getChannelName())
						.build()
				)
				.build()
			)
			.collect(Collectors.toList());
	}

	private List<WatchsResponse> convertWatchToWatchResponses(List<Watch> watches) {
		return watches.stream()
			.map(watch -> WatchsResponse.builder()
				.videoId(watch.getVideo().getVideoId())
				.videoName(watch.getVideo().getVideoName())
				.thumbnailFile(getThumbnailUrl(watch.getVideo().getChannel().getMember().getMemberId(), watch.getVideo().getThumbnailFile()))
				.modifiedDate(watch.getModifiedDate())
				.channel(WatchsResponse.Channel.builder()
					.memberId(watch.getVideo().getChannel().getMember().getMemberId())
					.channelName(watch.getVideo().getChannel().getChannelName())
					.build())
				.build())
			.collect(Collectors.toList());
	}
}
