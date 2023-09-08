package com.server.search.engine;

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

import com.server.global.testhelper.ServiceTest;
import com.server.search.repository.dto.VideoSearchResult;

public class MySQLSearchEngineTest extends ServiceTest {
	private static final String KEYWORD = "검";
	private static final int LIMIT = 3;

	NativeQueryTupleTransformer queryTupleTransformer = new NativeQueryTupleTransformer();

	@Autowired
	@Qualifier("mysql")
	private SearchEngine searchEngine;

	// @Test
	// @DisplayName("비디오 검색 테스트")
	// void searchVideos() {
	// 	given(mockVideoRepository.searchVideoByKeyword(KEYWORD, LIMIT)).willReturn(createTupleList());
	//
	// 	List<VideoSearchResult> videoSearchResults = searchEngine.searchVideos("공부", 3);
	//
	// 	System.out.println("!");
	// }

	private List<Tuple> createTupleList() {

		Object[] objects1 = new Object[]{
			new BigInteger("1"),
			"1/video1.png",
			"촛불로 공부하기",
			new BigInteger("1")
		};

		Object[] objects2 = new Object[]{
			new BigInteger("2"),
			"2/video1.png",
			"촛불로 공부하기",
			new BigInteger("2")
		};

		Object[] objects3 = new Object[]{
			new BigInteger("3"),
			"3/video1.png",
			"촛불로 공부하기",
			new BigInteger("3")
		};

		String[] allias = new String[]{
			"video_id",
			"thumbnail_file",
			"video_name",
			"member_id"
		};

		List<Tuple> tuples = new ArrayList<>();

		tuples.add((Tuple)queryTupleTransformer.transformTuple(objects1, allias));
		tuples.add((Tuple)queryTupleTransformer.transformTuple(objects2, allias));
		tuples.add((Tuple)queryTupleTransformer.transformTuple(objects3, allias));

		return tuples;
	}
}
