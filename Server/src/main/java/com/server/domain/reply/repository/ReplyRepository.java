package com.server.domain.reply.repository;

import com.server.domain.reply.entity.Reply;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByVideo_VideoId(Long videoId, int star, Pageable pageable);

}

