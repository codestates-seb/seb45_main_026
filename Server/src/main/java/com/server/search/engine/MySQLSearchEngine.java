package com.server.search.engine;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import com.server.domain.member.repository.MemberRepository;
import com.server.domain.video.service.dto.request.VideoGetServiceRequest;
import com.server.domain.video.service.dto.response.VideoPageResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.video.repository.VideoRepository;
import com.server.module.s3.service.AwsService;
import com.server.module.s3.service.dto.FileType;
import com.server.search.engine.dto.ChannelResultResponse;
import com.server.search.engine.dto.ChannelSearchResponse;
import com.server.search.engine.dto.VideoChannelSearchResponse;
import com.server.search.engine.dto.VideoSearchResponse;
import com.server.search.repository.dto.ChannelSearchResult;
import com.server.search.repository.dto.VideoSearchResult;

@Service("mysql")
public class MySQLSearchEngine implements SearchEngine {

	private final VideoRepository videoRepository;
	private final ChannelRepository channelRepository;
	private final MemberRepository memberRepository;
	private final AwsService awsService;

	public MySQLSearchEngine(VideoRepository videoRepository, ChannelRepository channelRepository,
		MemberRepository memberRepository, AwsService awsService) {
		this.videoRepository = videoRepository;
		this.channelRepository = channelRepository;
		this.memberRepository = memberRepository;
		this.awsService = awsService;
	}

	private List<VideoSearchResult> searchVideos(String keyword, int limit) {

		List<Tuple> tuples = videoRepository.searchVideoByKeyword(keyword, limit);

		return VideoSearchResult.converter(tuples);
	}

	private List<VideoSearchResponse> resultsToVideoSearchResponse(List<VideoSearchResult> results) {
		return results.stream()
			.map(result -> VideoSearchResponse.builder()
				.videoId(result.getVideoId())
				.videoName(result.getVideoName())
				.thumbnailUrl(
					getImageUrl(result.getThumbnailFile(), FileType.THUMBNAIL)
				)
				.build()
			)
			.collect(Collectors.toList());
	}


	private List<ChannelSearchResult> searchChannels(String keyword, int limit) {

		List<Tuple> tuples = channelRepository.findChannelByKeyword(keyword, limit);

		return ChannelSearchResult.converter(tuples);

	}

	private List<ChannelSearchResponse> resultsToChannelSearchResponse(List<ChannelSearchResult> results) {
		return results.stream()
			.map(result -> ChannelSearchResponse.builder()
				.memberId(result.getMemberId())
				.channelName(result.getChannelName())
				.imageUrl(
					getImageUrl(result.getImageFile(), FileType.PROFILE_IMAGE)
				)
				.build()
			)
			.collect(Collectors.toList());
	}

	@Override
	public VideoChannelSearchResponse searchVideosAndChannels(String keyword, int limit) {

		List<VideoSearchResult> videoSearchResults = searchVideos(keyword, limit);
		List<VideoSearchResponse> videoSearchResponses = resultsToVideoSearchResponse(videoSearchResults);

		List<ChannelSearchResult> channelSearchResults = searchChannels(keyword, limit);
		List<ChannelSearchResponse> channelSearchResponses = resultsToChannelSearchResponse(channelSearchResults);

		return VideoChannelSearchResponse.builder()
			.videos(videoSearchResponses)
			.channels(channelSearchResponses)
			.build();
	}

	public Page<ChannelResultResponse> searchChannelResults(String keyword, int page, int size, String sort, Long loginId) {

		Pageable pageable = setPageable(page, size, sort);

		Page<Tuple> pages = channelRepository.findChannelResultByKeyword(keyword, pageable);

		Page<ChannelResultResponse> channelResultResponses = resultTupleToChannelSearchResult(pages);

		setIsSubscribedForChannel(channelResultResponses, loginId);

		return channelResultResponses;
	}

	private Pageable setPageable(int page, int size, String sort) {
		String orderBy;
		Pageable pageable;

		switch (sort) {
			case "name":
				orderBy = "channel_name";
				break;
			case "subscribers":
				orderBy = "subscribers";
				break;
			default:
				orderBy = null;
				break;
		}

		if (orderBy != null) {
			pageable = PageRequest.of(page - 1, size, Sort.by(orderBy).descending());
		} else {
			pageable = PageRequest.of(page - 1, size);
		}
		return pageable;
	}

	private Page<ChannelResultResponse> resultTupleToChannelSearchResult(Page<Tuple> pages) {
		return pages.map(
			tuple -> ChannelResultResponse.builder()
				.channelId(tuple.get(0, BigInteger.class).longValue())
				.channelName(tuple.get(1, String.class))
				.description(tuple.get(2, String.class))
				.subscribers(tuple.get(3, Integer.class))
				.imageUrl(getImageUrl(tuple.get(4, String.class), FileType.PROFILE_IMAGE))
				.build()
		);
	}

	private void setIsSubscribedForChannel(Page<ChannelResultResponse> channelResultResponses, Long loginId) {
		List<Long> ownerMemberIds = channelResultResponses.stream()
			.map(ChannelResultResponse::getChannelId).collect(Collectors.toList());

		List<Boolean> isSubscribedList = memberRepository.checkMemberSubscribeChannel(loginId, ownerMemberIds);

		channelResultResponses.forEach(
			channelResultResponse -> {
				int currentIndex = channelResultResponses.getContent().indexOf(channelResultResponse);
				boolean subscribed = isSubscribedList.get(currentIndex);

				channelResultResponse.setIsSubscribed(subscribed);
			}
		);
	}

	private String getImageUrl(String fileName, FileType fileType) {
		return awsService.getFileUrl(fileName, fileType);
	}
}
