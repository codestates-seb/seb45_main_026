package com.server.domain.reply.repository;

import com.server.domain.reply.entity.Reply;
import com.server.global.reponse.ApiPageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findRepliesBy(Long videoId, int star, Pageable pageable);

    Optional<Reply> findById(Long memberId);

}

