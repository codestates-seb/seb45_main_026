package com.server.domain.question.entity;

import com.server.domain.answer.entity.Answer;
import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
public class Question extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long questionId;

    private int position;

    @Lob
    private String content;

    @OneToMany(mappedBy = "question")
    private List<Answer> answers = new ArrayList<>();

    @Lob
    private String description;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "video_id")
    private Video video;



}
