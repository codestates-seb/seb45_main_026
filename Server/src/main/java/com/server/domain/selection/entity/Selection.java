package com.server.domain.selection.entity;

import com.server.domain.question.entity.Question;
import com.server.global.entity.BaseEntity;
import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
public class Selection extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long selectionId;

    private String content;

    @ManyToOne(fetch = LAZY)
    private Question question;
}
