package com.server.domain.reply.repository;

import com.server.domain.reply.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    Page<Reply> findAllByReplyId(Pageable pageable, String sort, Long replyId);



}

/*
Page<Video> findAllByCategoryPaging(
String category, Pageable pageable, String sort, Long memberId, boolean subscribe);

findAll: 엔티티 전체 조회
ByCategory: category 필드를 기준으로 조회를 수행
Paging: 페이지네이션을 적용하여 결과를 반환
category: 조회할 데이터의 카테고리를 지정
pageable: 페이지네이션을 처리하기 위한 페이지 정보
 */