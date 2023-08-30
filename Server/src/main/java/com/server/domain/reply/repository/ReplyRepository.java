package com.server.domain.reply.repository;

import com.server.domain.member.entity.Member;
import com.server.domain.reply.dto.ReplyDto;
import com.server.domain.reply.entity.Reply;
import com.server.domain.video.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    Page<Reply> findReplyBy(Reply reply, Pageable pageable);

    boolean findByMemberAndVideo(Member member, Video video);


}

