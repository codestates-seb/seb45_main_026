package com.server.domain.video.repository;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SearchRepositoryTest extends RepositoryTest {
	@Container
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0.23");

	@Autowired
	VideoRepository videoRepository;

	@Test
	@DisplayName("테스트 컨테이너의 MySQL 연동 확인 테스트")
	void test1() {
		log.info("로그 getJdbcDriverInstance {} ", mySQLContainer.getJdbcDriverInstance());
		log.info("로그 getJdbcUrl {} ", mySQLContainer.getJdbcUrl());
		log.info("로그 getMappedPort {} ", mySQLContainer.getMappedPort(3306));
		log.info("로그 getHost {} ", mySQLContainer.getHost());
		log.info("로그 getUsername {} ", mySQLContainer.getUsername());
		log.info("로그 getPassword {} ", mySQLContainer.getPassword());
	}

	// @Test
	// @DisplayName("비디오 검색 테스트")
	// void videoSearch() {
	// 	Member member = createAndSaveMember();
	// 	Channel channel = createAndSaveChannel(member);
	// 	createAndSaveVideoWithName(channel, "가나다라마바사");
	// 	createAndSaveVideoWithName(channel, "나다라마바사가");
	// 	createAndSaveVideoWithName(channel, "사가나다라마바");
	// 	createAndSaveVideoWithName(channel, "마사나다라마가");
	// 	createAndSaveVideoWithName(channel, "다라가나마바사");
	// 	createAndSaveVideoWithName(channel, "가라마바나다사");
	// 	createAndSaveVideoWithName(channel, "가가가가가가가");
	//
	//
	// 	List<Video> videos = videoRepository.searchVideos("나");
	//
	// 	for (Video video : videos) {
	// 		System.out.println(video.getVideoName());
	// 	}
	// }
}
