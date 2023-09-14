package com.server.search.engine;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;

import org.hibernate.jpa.spi.NativeQueryTupleTransformer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.video.repository.VideoRepository;
import com.server.global.testhelper.ServiceTest;
import com.server.search.engine.dto.ChannelResultResponse;
import com.server.search.engine.dto.ChannelSearchResponse;
import com.server.search.engine.dto.VideoChannelSearchResponse;
import com.server.search.engine.dto.VideoSearchResponse;
import com.server.search.repository.dto.VideoSearchResult;

public class MySQLSearchEngineTest extends ServiceTest {
	private static final String KEYWORD = "공";
	private static final int LIMIT = 3;

	NativeQueryTupleTransformer queryTupleTransformer = new NativeQueryTupleTransformer();

	@Autowired
	@Qualifier("mysql")
	private SearchEngine searchEngine;
	@MockBean
	private VideoRepository mockVideoRepository;
	@MockBean
	private ChannelRepository mockChannelRepository;

	@Test
	@DisplayName("비디오 및 채널 검색 테스트")
	void searchVideosAndChannels() {
		List<Tuple> videoTuple = createVideoTupleList();
		List<Tuple> channelTuple = createChannelTupleList();

		given(mockVideoRepository.searchVideoByKeyword(KEYWORD, LIMIT)).willReturn(videoTuple);
		given(mockChannelRepository.findChannelByKeyword(KEYWORD, LIMIT)).willReturn(channelTuple);

		VideoChannelSearchResponse videoChannelSearchResults = searchEngine.searchVideosAndChannels(KEYWORD, LIMIT);

		assertThat(videoChannelSearchResults.getVideos().size(), equalTo(LIMIT));
		assertThat(videoChannelSearchResults.getChannels().size(), equalTo(LIMIT));
	}

	@Test
	@DisplayName("채널 검색 테스트")
	void searchChannels() {
		List<Tuple> channelTuple = createChannelResultTupleList();
		Page<Tuple> channelPageTuple = new PageImpl<>(channelTuple);

		given(mockChannelRepository.findChannelResultByKeyword(KEYWORD, PageRequest.of(0, 3)))
			.willReturn(channelPageTuple);

		Page<ChannelResultResponse> channelResultResponses =
			searchEngine.searchChannelResults(KEYWORD, 1, 3, "default", 1L);

		assertThat(channelResultResponses.getContent().size(), equalTo(3));
	}

	private List<Tuple> createVideoTupleList() {

		Object[] objects1 = new Object[]{
			new BigInteger("1"),
			"1/video1.png",
			"촛불로 공부하기",
			new BigInteger("1")
		};

		Object[] objects2 = new Object[]{
			new BigInteger("2"),
			"2/video1.png",
			"모닥불로 공부하기",
			new BigInteger("2")
		};

		Object[] objects3 = new Object[]{
			new BigInteger("3"),
			"3/video1.png",
			"불로 공부하기",
			new BigInteger("3")
		};

		String[] allias = new String[]{
			"video_id",
			"thumbnail_file",
			"video_name",
			"channel_id"
		};

		List<Tuple> tuples = new ArrayList<>();

		tuples.add((Tuple)queryTupleTransformer.transformTuple(objects1, allias));
		tuples.add((Tuple)queryTupleTransformer.transformTuple(objects2, allias));
		tuples.add((Tuple)queryTupleTransformer.transformTuple(objects3, allias));

		return tuples;
	}

	private List<Tuple> createChannelTupleList() {

		Object[] objects1 = new Object[]{
			"공부 채널1",
			new BigInteger("1"),
			"image1"
		};

		Object[] objects2 = new Object[]{
			"공부 채널2",
			new BigInteger("2"),
			"image2"
		};

		Object[] objects3 = new Object[]{
			"공부 채널3",
			new BigInteger("3"),
			"image3"
		};

		String[] allias = new String[]{
			"channel_name",
			"member_id",
			"image_file"
		};

		List<Tuple> tuples = new ArrayList<>();

		tuples.add((Tuple)queryTupleTransformer.transformTuple(objects1, allias));
		tuples.add((Tuple)queryTupleTransformer.transformTuple(objects2, allias));
		tuples.add((Tuple)queryTupleTransformer.transformTuple(objects3, allias));

		return tuples;
	}

	private List<Tuple> createChannelResultTupleList() {

		Object[] objects1 = new Object[]{
			new BigInteger("1"),
			"공부 채널1",
			"채널 설명",
			1,
			"image1"
		};

		Object[] objects2 = new Object[]{
			new BigInteger("2"),
			"공부 채널2",
			"채널 설명",
			2,
			"image2"
		};

		Object[] objects3 = new Object[]{
			new BigInteger("3"),
			"공부 채널3",
			"채널 설명",
			3,
			"image3"
		};

		String[] allias = new String[]{
			"channel_id",
			"channel_name",
			"description",
			"subscribers",
			"image_file"
		};

		List<Tuple> tuples = new ArrayList<>();

		tuples.add((Tuple)queryTupleTransformer.transformTuple(objects1, allias));
		tuples.add((Tuple)queryTupleTransformer.transformTuple(objects2, allias));
		tuples.add((Tuple)queryTupleTransformer.transformTuple(objects3, allias));

		return tuples;
	}
}
