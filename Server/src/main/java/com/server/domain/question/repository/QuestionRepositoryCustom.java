package com.server.domain.question.repository;

import com.server.domain.question.repository.dto.QuestionData;
import com.server.domain.video.entity.Video;

import java.util.Optional;

public interface QuestionRepositoryCustom {

    Optional<QuestionData> findQuestionDataWithMemberAnswer(Long MemberId, Long questionId);

    Optional<Video> findVideoByQuestionId(Long questionId);
}
