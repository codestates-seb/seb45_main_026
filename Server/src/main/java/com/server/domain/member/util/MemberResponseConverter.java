package com.server.domain.member.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.server.domain.cart.entity.Cart;
import com.server.domain.category.service.dto.response.CategoryResponse;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.dto.response.CartsResponse;
import com.server.domain.member.service.dto.response.OrdersResponse;
import com.server.domain.member.service.dto.response.PlaylistChannelDetailsResponse;
import com.server.domain.member.service.dto.response.PlaylistChannelResponse;
import com.server.domain.member.service.dto.response.PlaylistsResponse;
import com.server.domain.member.service.dto.response.SubscribesResponse;
import com.server.domain.member.service.dto.response.WatchsResponse;
import com.server.domain.order.entity.Order;
import com.server.domain.video.entity.Video;
import com.server.domain.watch.entity.Watch;
import com.server.module.s3.service.AwsService;
import com.server.module.s3.service.dto.FileType;

@Component
public class MemberResponseConverter {

	private final AwsService awsService;

	public MemberResponseConverter(AwsService awsService) {
		this.awsService = awsService;
	}

	public Page<SubscribesResponse> convertSubscribesToSubscribesResponse(Page<Channel> channels) {

			return channels.map(channel -> {
						Member member = channel.getMember();

						return SubscribesResponse.builder()
							.memberId(member.getMemberId())
							.channelName(channel.getChannelName())
							.subscribes(Optional.ofNullable(channel.getSubscribes())
								.map(List::size)
								.orElse(0))
							.imageUrl(getProfileUrl(member.getImageFile()))
							.build();
					}
			);
	}

	public Page<OrdersResponse> convertOrdersToOrdersResponses(Page<Order> orders) {
		return orders.map(order -> OrdersResponse.builder()
			.orderId(order.getOrderId())
			.amount(order.getTotalPayAmount())
			.orderCount(order.getOrderVideos().size())
			.orderStatus(order.getOrderStatus())
			.createdDate(order.getCreatedDate())
			.completedDate(order.getCompletedDate())
			.orderVideos(order.getOrderVideos().stream()
				.map(orderVideo -> {
					String channelName;
					Channel channel = orderVideo.getVideo().getChannel();

					if (channel == null) {
						channelName = "삭제된 채널";
					}
					else {
						channelName = channel.getChannelName();
					}

					return OrdersResponse.OrderVideo.builder()
						.videoId(orderVideo.getVideo().getVideoId())
						.videoName(orderVideo.getVideo().getVideoName())
						.thumbnailFile(getThumbnailUrl(orderVideo.getVideo().getThumbnailFile()))
						.channelName(channelName)
						.price(orderVideo.getVideo().getPrice())
						.orderStatus(orderVideo.getOrderStatus())
						.build();
				})
				.collect(Collectors.toList()))
			.build());
	}

	public Page<PlaylistsResponse> convertVideosToPlaylistsResponses(Page<Video> videos) {
		return videos.map(video -> {
			Channel channel = video.getChannel();

			if (channel == null) {
				return PlaylistsResponse.builder()
					.videoId(video.getVideoId())
					.videoName(video.getVideoName())
					.thumbnailUrl(
						getThumbnailUrl(video.getThumbnailFile())
					)
					.star(video.getStar())
					.createdDate(video.getCreatedDate())
					.modifiedDate(video.getModifiedDate())
					.channel(
						PlaylistsResponse.Channel.builder()
							.channelName("삭제된 채널")
							.build()
					)
					.build();
			}

			Member member = channel.getMember();
			Long memberId = member.getMemberId();

			return PlaylistsResponse.builder()
				.videoId(video.getVideoId())
				.videoName(video.getVideoName())
				.thumbnailUrl(
					getThumbnailUrl(video.getThumbnailFile())
				)
				.star(video.getStar())
				.createdDate(video.getCreatedDate())
				.modifiedDate(video.getModifiedDate())
				.channel(
					PlaylistsResponse.Channel.builder()
						.memberId(video.getChannel().getMember().getMemberId())
						.channelName(video.getChannel().getChannelName())
						.imageUrl(getProfileUrl(member.getImageFile()))
						.build()
				)
				.build();
			}
		);
	}

	public Page<WatchsResponse> convertWatchToWatchResponses(Page<Watch> watches, List<Boolean> isPurchased) {
		Page<WatchsResponse> watchsResponses = watches.map(watch -> {
				Channel channel = watch.getVideo().getChannel();

				int currentIndex = watches.getContent().indexOf(watch);
				boolean purchased = isPurchased.get(currentIndex);

				if (channel == null) {
					return WatchsResponse.builder()
						.videoId(watch.getVideo().getVideoId())
						.videoName(watch.getVideo().getVideoName())
						.thumbnailUrl(getThumbnailUrl(watch.getVideo().getThumbnailFile()))
						.modifiedDate(watch.getModifiedDate())
						.star(watch.getVideo().getStar())
						.price(watch.getVideo().getPrice())
						.isPurchased(purchased)
						.channel(WatchsResponse.Channel.builder()
							.channelName("삭제된 채널")
							.build())
						.build();
				}

				Member member = channel.getMember();
				Long memberId = member.getMemberId();

				return WatchsResponse.builder()
					.videoId(watch.getVideo().getVideoId())
					.videoName(watch.getVideo().getVideoName())
					.thumbnailUrl(getThumbnailUrl(watch.getVideo().getThumbnailFile()))
					.modifiedDate(watch.getModifiedDate())
					.star(watch.getVideo().getStar())
					.price(watch.getVideo().getPrice())
					.isPurchased(purchased)
					.channel(WatchsResponse.Channel.builder()
						.memberId(memberId)
						.channelName(watch.getVideo().getChannel().getChannelName())
						.imageUrl(getProfileUrl(member.getImageFile()))
						.build())
					.build();
			}
		);

		return watchsResponses;
	}

	public Page<CartsResponse> convertCartToCartResponse(Page<Cart> carts) {

		return carts.map(cart -> {
			Video video = cart.getVideo();
			Channel channel = video.getChannel();
			Member member = video.getChannel().getMember();

			CartsResponse.Channel channelInfo = CartsResponse.Channel.builder()
				.memberId(member.getMemberId())
				.channelName(channel.getChannelName())
				.subscribes(channel.getSubscribers())
				.imageUrl(getProfileUrl(member.getImageFile()))
				.build();

			return CartsResponse.builder()
				.videoId(video.getVideoId())
				.videoName(video.getVideoName())
				.thumbnailUrl(getThumbnailUrl(video.getThumbnailFile()))
				.views(video.getView())
				.createdDate(video.getCreatedDate())
				.price(cart.getPrice())
				.videoCategories(video.getVideoCategories().stream().map(
					videoCategory -> CategoryResponse.of(videoCategory.getCategory())
				)
				.collect(Collectors.toList()))
				.channel(channelInfo)
				.build();
		});
	}

	public Page<PlaylistChannelResponse> convertChannelToPlaylistChannelResponse(Page<Tuple> result) {

		return result.map(tuple -> {
				Long memberId = tuple.get(0, Long.class);

				if (memberId == null || memberId == 0) {
					return PlaylistChannelResponse.builder()
						.channelName("삭제된 채널")
						.build();
				}

				String channelName = tuple.get(1, String.class);
				String imageFile = tuple.get(2, String.class);
				Long videoCount = tuple.get(3, Long.class);
				Boolean isSubscribed = tuple.get(4, Boolean.class);
				Integer subscribers = tuple.get(5, Integer.class);

				return PlaylistChannelResponse.builder()
					.memberId(memberId)
					.channelName(channelName)
					.imageUrl(getProfileUrl(imageFile))
					.videoCount(videoCount)
					.subscribers(subscribers)
					.isSubscribed(isSubscribed)
					.build();
			});
	}

	public Page<PlaylistChannelDetailsResponse> convertVideoToPlaylistChannelDetailsResponse(Page<Video> videos) {
		return videos.map(
			video -> PlaylistChannelDetailsResponse.builder()
				.videoId(video.getVideoId())
				.videoName(video.getVideoName())
				.description(video.getDescription())
				.thumbnailUrl(getThumbnailUrl(video.getThumbnailFile()))
				.view(video.getView())
				.star(video.getStar())
				.createdDate(video.getCreatedDate())
				.build()
		);
	}

	private String getThumbnailUrl(String thumbnailFile) {
		return awsService.getFileUrl(thumbnailFile, FileType.THUMBNAIL);
	}

	private String getProfileUrl(String profileImageName) {
			return awsService.getFileUrl(
				profileImageName,
				FileType.PROFILE_IMAGE
			);
	}
}
