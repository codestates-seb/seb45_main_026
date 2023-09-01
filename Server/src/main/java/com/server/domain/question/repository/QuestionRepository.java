package com.server.domain.question.repository;

import com.server.domain.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {

    @Query("select q from Question q " +
            "join fetch q.video v " +
            "where v.videoId = :videoId")
    List<Question> findQuestionsWithVideoByVideoId(Long videoId);
}