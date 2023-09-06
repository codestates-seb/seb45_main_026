package com.server.search.engine;

import static com.server.domain.channel.entity.QChannel.*;
import static com.server.domain.video.entity.QVideo.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.channel.entity.Channel;
import com.server.domain.video.entity.Video;
import com.server.search.engine.dto.VideoChannelSearchResponse;

@Service("mysql")
public class MySQLSearchEngine implements SearchEngine {

	private final JdbcTemplate jdbcTemplate;

	public MySQLSearchEngine(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Video> searchVideos(String keyword) {

		String sqlForVideo = "SELECT video.*, channel.*, member.* " +
			"FROM video " +
			"INNER JOIN channel ON video.channel_id = channel.channel_id " +
			"INNER JOIN member ON channel.member_id = member.member_id " +
			"WHERE MATCH(video_name) AGAINST (? IN BOOLEAN MODE) " +
			"AND video_status != 'CLOSED' " +
			"LIMIT 10";

		return jdbcTemplate.query(
			sqlForVideo,
			new Object[]{keyword},
			(rs, rowNum) -> Video.builder()
				.videoId(rs.getLong("video_id"))
				.videoName(rs.getString("video_name"))
				.thumbnailFile(rs.getString("thumbnail_file"))
				.view(rs.getInt("view"))
				.build()
		);
	}

	@Override
	public List<Channel> searchChannels(String keyword) {

		String sqlForChannel = "SELECT channel.*, member.* FROM channel " +
			"INNER JOIN member ON channel.member_id = member.member_id " +
			"WHERE MATCH(channel_name) AGAINST (? IN BOOLEAN MODE) " +
			"LIMIT 10";

		return jdbcTemplate.query(
			sqlForChannel,
			new Object[]{keyword},
			(rs, rowNum) -> Channel.builder()
				.channelId(rs.getLong("channel_id"))
				.channelName(rs.getString("channel_name"))
				.build()
		);
	}

	@Override
	public VideoChannelSearchResponse searchVideosAndChannels(String keyword) {

		List<Video> videos = searchVideos(keyword);
		List<Channel> channels = searchChannels(keyword);

		return VideoChannelSearchResponse.builder()
			.videos(videos)
			.channels(channels)
			.build();
	}
}
