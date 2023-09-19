package com.server.domain.question.entity;

import com.server.domain.answer.entity.Answer;
import com.server.domain.reward.entity.Rewardable;
import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Comparator.*;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.*;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder
public class Question extends BaseEntity implements Rewardable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long questionId;

    private int position;

    @Lob
    private String content;

    private String questionAnswer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();

    @ElementCollection
    @OrderColumn(name = "selection_order")
    private List<String> selections = new ArrayList<>();

    @Lob
    private String description;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    public static Question createQuestion(int position, String content, String questionAnswer, String description, List<String> selections, Video video) {
        return Question.builder()
                .position(position)
                .content(content)
                .questionAnswer(questionAnswer)
                .description(description)
                .selections(selections)
                .video(video)
                .build();
    }


    public void update(String content, String questionAnswer, String description, List<String> selections) {
        this.content = content == null ? this.content : content;
        this.questionAnswer = questionAnswer == null ? this.questionAnswer : questionAnswer;
        this.description = description == null ? this.description : description;
        this.selections = selections == null ? this.selections : selections;
    }

    public int getRewardPoint(){
        return 10;
    }

    public void sortExceptThis() {
        this.position = 0;

        List<Question> sortedQuestions = this.video.getQuestions().stream()
                .sorted(comparingInt(Question::getPosition)).collect(Collectors.toList());

        IntStream.range(0, sortedQuestions.size())
                .forEach(i -> sortedQuestions.get(i).position = i);
    }
}
