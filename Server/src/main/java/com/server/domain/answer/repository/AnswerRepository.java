package com.server.domain.answer.repository;

import com.server.domain.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query("select a from Answer a " +
            "join fetch a.member m " +
            "join fetch a.question q " +
            "where m.memberId = :memberId and q.questionId = :questionId")
    Optional<Answer> findByMemberIdAndQuestionId(Long memberId, Long questionId);

    @Query("select a from Answer a " +
            "join fetch a.member m " +
            "join fetch a.question q " +
            "where m.memberId = :memberId and q.questionId in :questionIds")
    List<Answer> findByMemberIdAndQuestionIds(Long memberId, List<Long> questionIds);
}