package com.server.search.engine;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.springframework.stereotype.Service;

import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.video.repository.VideoRepository;
import com.server.module.s3.service.AwsService;
import com.server.module.s3.service.dto.FileType;
import com.server.search.engine.dto.ChannelSearchResponse;
import com.server.search.engine.dto.VideoChannelSearchResponse;
import com.server.search.engine.dto.VideoSearchResponse;
import com.server.search.repository.dto.ChannelSearchResult;
import com.server.search.repository.dto.VideoSearchResult;

@Service("mysql")
public class MySQLSearchEngine implements SearchEngine {

	private final VideoRepository videoRepository;
	private final ChannelRepository channelRepository;
	private final AwsService awsService;

	public MySQLSearchEngine(VideoRepository videoRepository, ChannelRepository channelRepository,
		AwsService awsService) {
		this.videoRepository = videoRepository;
		this.channelRepository = channelRepository;
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

	private String getImageUrl(String fileName, FileType fileType) {
		return awsService.getFileUrl(fileName, fileType);
	}
}
