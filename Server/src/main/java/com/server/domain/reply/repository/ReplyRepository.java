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

findAll: 엔티티를 전체 조회하는 것을 의미합니다.
ByCategory: category 필드를 기준으로 조회를 수행하는 조건을 나타냅니다.
Paging: 페이지네이션을 적용하여 결과를 반환하는 것을 의미합니다. 페이지네이션은 데이터를 일정한 크기로 나누어 조회하는 것을 말합니다.
category: 조회할 데이터의 카테고리를 지정하는 필드입니다.
pageable: 페이지네이션을 처리하기 위한 페이지 정보를 담고 있는 객체입니다.
sort: 결과 정렬 방식을 지정하는 문자열입니다.
memberId: 회원의 식별자를 나타냅니다.
subscribe: 구독 여부를 나타내는 boolean 값입니다.
 */