package com.server.domain.question.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.question.entity.Question;
import com.server.domain.question.repository.dto.QQuestionData;
import com.server.domain.question.repository.dto.QuestionData;
import com.server.domain.video.entity.Video;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.server.domain.answer.entity.QAnswer.answer;
import static com.server.domain.channel.entity.QChannel.channel;
import static com.server.domain.member.entity.QMember.member;
import static com.server.domain.question.entity.QQuestion.question;
import static com.server.domain.video.entity.QVideo.video;

public class QuestionRepositoryImpl implements QuestionRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public QuestionRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public Optional<QuestionData> findQuestionDataWithMemberAnswer(Long memberId, Long questionId) {
        Optional<QuestionData> questionData = Optional.ofNullable(
                queryFactory
                        .select(new QQuestionData(
                                question.questionId,
                                question.position,
                                question.content,
                                question.questionAnswer,
                                answer.answerId,        // 해당 Answer가 없으면 null 반환
                                answer.myAnswer,       // 해당 Answer가 없으면 null 반환
                                answer.answerStatus,   // 해당 Answer가 없으면 null 반환
                                question.description,
                                answer.modifiedDate    // 해당 Answer가 없으면 null 반환
                        ))
                        .from(question)
                        .leftJoin(question.answers, answer).on(answer.member.memberId.eq(memberId))
                        .where(question.questionId.eq(questionId))
                        .fetchOne()
        );

        Question findQuestion = queryFactory
                .selectFrom(question)
                .leftJoin(question.selections).fetchJoin()
                .where(question.questionId.eq(questionId)).fetchOne();
        if(findQuestion != null) {
            questionData.ifPresent(data -> data.setSelections(
                    findQuestion.getSelections()
            ));
        }

        return questionData;
    }

    @Override
    public List<QuestionData> findQuestionDatasWithMemberAnswerByVideoId(Long memberId, Long videoId) {

        List<QuestionData> questionDatas = queryFactory
                .select(new QQuestionData(
                        question.questionId,
                        question.position,
                        question.content,
                        question.questionAnswer,
                        answer.answerId,        // 해당 Answer가 없으면 null 반환
                        answer.myAnswer,       // 해당 Answer가 없으면 null 반환
                        answer.answerStatus,   // 해당 Answer가 없으면 null 반환
                        question.description,
                        answer.modifiedDate    // 해당 Answer가 없으면 null 반환
                ))
                .from(video)
                .join(video.questions, question)
                .leftJoin(question.answers, answer).on(answer.member.memberId.eq(memberId))
                .where(video.videoId.eq(videoId))
                .fetch();

        List<Long> questionIds = questionDatas.stream()
                .map(QuestionData::getQuestionId)
                .collect(Collectors.toList());

        List<Question> questions = queryFactory
                .selectFrom(question)
                .leftJoin(question.selections).fetchJoin()
                .where(question.questionId.in(questionIds)).fetch();

        questionDatas.forEach(data -> {
            questions.stream()
                    .filter(question -> question.getQuestionId().equals(data.getQuestionId()))
                    .findFirst()
                    .ifPresent(question -> data.setSelections(question.getSelections()));
        });

        return questionDatas;
    }

    public Optional<Video> findVideoByQuestionId(Long questionId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(video)
                        .join(video.questions, question)
                        .join(video.channel, channel).fetchJoin()
                        .join(channel.member, member).fetchJoin()
                        .where(question.questionId.eq(questionId))
                        .fetchOne()
        );
    }



}
