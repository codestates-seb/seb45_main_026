package com.server.domain.channel.respository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

import javax.persistence.Tuple;

public interface ChannelRepository extends JpaRepository<Channel, Long>, ChannelRepositoryCustom {

	@Query(value = "SELECT c.channel_name, m.member_id, m.image_file " +
		"FROM channel c JOIN member m ON c.member_id = m.member_id " +
		"WHERE MATCH(c.channel_name) AGAINST(?1 IN BOOLEAN MODE) LIMIT ?2", nativeQuery = true)
	List<Tuple> findChannelByKeyword(String keyword, int limit);

}
