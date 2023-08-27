package com.server.domain.selection.entity;

import com.server.domain.question.entity.Question;
import com.server.global.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Selection extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long selectionId;

    private int position;

    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "question_id")
    private Question question;
}
