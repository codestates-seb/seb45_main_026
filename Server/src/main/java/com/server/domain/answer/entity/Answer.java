package com.server.domain.answer.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.question.entity.Question;
import com.server.global.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Answer extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long answerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnswerStatus answerStatus;

    private String myAnswer;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    public static Answer createAnswer(String myAnswer, Member member, Question question) {
        return Answer.builder()
                .answerStatus(AnswerStatus.WRONG)
                .myAnswer(myAnswer)
                .member(member)
                .question(question)
                .build();
    }

    public boolean solveAnswer(String myAnswer, String questionAnswer) {

        this.myAnswer = myAnswer;

        if (myAnswer.equals(questionAnswer)) {
            this.answerStatus = AnswerStatus.CORRECT;
            return true;
        } else {
            this.answerStatus = AnswerStatus.WRONG;
            return false;
        }
    }

}
