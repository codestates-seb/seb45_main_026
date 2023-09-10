package com.server.domain.reward.entity;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.question.entity.Question;
import com.server.domain.reply.entity.Reply;
import com.server.domain.video.entity.Video;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

class RewardTest {

    @TestFactory
    @DisplayName("리워드를 생성한다.")
    Collection<DynamicTest> createReward() {
        //given
        Member member = createMember();
        Video video = createVideo(member);
        Question question = createQuestion(video);
        Reply reply = createReply(video);

        return List.of(
                dynamicTest("VideoReward 를 생성하고 member 의 리워드를 적립한다.", () -> {
                    //given
                    int beforeReward = member.getReward();

                    //when
                    NewReward reward = NewReward.createReward(video.getRewardPoint(), member, video);

                    //then
                    assertAll("member 의 리워드 적립을 확인한다.",
                            () -> assertThat(member.getReward()).isEqualTo(beforeReward + video.getRewardPoint())
                    );

                    assertAll("리워드의 정보를 확인한다.",
                            () -> assertThat(reward instanceof VideoReward).isTrue(),
                            () -> assertThat(reward.getRewardPoint()).isEqualTo(video.getRewardPoint()),
                            () -> assertThat(reward.getMember()).isEqualTo(member),
                            () -> assertThat(reward.isCanceled).isEqualTo(false)
                    );
                }),
                dynamicTest("QuestionReward 를 생성하고 member 의 리워드를 적립한다.", () -> {
                    //given
                    int beforeReward = member.getReward();

                    //when
                    NewReward reward = NewReward.createReward(question.getRewardPoint(), member, question);

                    //then
                    assertAll("member 의 리워드 적립을 확인한다.",
                            () -> assertThat(member.getReward()).isEqualTo(beforeReward + question.getRewardPoint())
                    );

                    assertAll("리워드의 정보를 확인한다.",
                            () -> assertThat(reward instanceof QuestionReward).isTrue(),
                            () -> assertThat(reward.getRewardPoint()).isEqualTo(question.getRewardPoint()),
                            () -> assertThat(reward.getMember()).isEqualTo(member),
                            () -> assertThat(reward.isCanceled).isEqualTo(false)
                    );
                }),
                dynamicTest("ReplyReward 를  생성하고 member 의 리워드를 적립한다.", () -> {
                    //given
                    int beforeReward = member.getReward();

                    //when
                    NewReward reward = NewReward.createReward(reply.getRewardPoint(), member, reply);

                    //then
                    assertAll("member 의 리워드 적립을 확인한다.",
                            () -> assertThat(member.getReward()).isEqualTo(beforeReward + reply.getRewardPoint())
                    );

                    assertAll("리워드의 정보를 확인한다.",
                            () -> assertThat(reward instanceof ReplyReward).isTrue(),
                            () -> assertThat(reward.getRewardPoint()).isEqualTo(reply.getRewardPoint()),
                            () -> assertThat(reward.getMember()).isEqualTo(member),
                            () -> assertThat(reward.isCanceled).isEqualTo(false)
                    );
                })
        );
    }

    @Test
    @DisplayName("리워드를 취소한다.")
    void cancelReward() {
        //given
        Member member = createMember();
        Video video = createVideo(member);
        Question question = createQuestion(video);
        Reply reply = createReply(video);

        NewReward videoReward = NewReward.createReward(video.getRewardPoint(), member, video);
        NewReward questionReward = NewReward.createReward(question.getRewardPoint(), member, question);
        NewReward replyReward = NewReward.createReward(reply.getRewardPoint(), member, reply);

        int beforeReward = member.getReward();

        //when
        videoReward.cancelReward();
        questionReward.cancelReward();
        replyReward.cancelReward();

        //then
        assertAll("리워드를 취소한다.",
                () -> assertThat(videoReward.isCanceled).isEqualTo(true),
                () -> assertThat(questionReward.isCanceled).isEqualTo(true),
                () -> assertThat(replyReward.isCanceled).isEqualTo(true)
        );

        int totalCancelReward = videoReward.getRewardPoint()
                + questionReward.getRewardPoint()
                + replyReward.getRewardPoint();

        assertAll("member 의 리워드를 차감한다.",
                () -> assertThat(member.getReward()).isEqualTo(beforeReward - totalCancelReward)
        );
    }

    private Member createMember() {

        Channel channel = Channel.builder().build();

        return Member.builder()
                .channel(channel)
                .build();
    }

    private Video createVideo(Member member) {
        return Video.builder()
                .channel(member.getChannel())
                .build();
    }

    private Question createQuestion(Video video) {
        return Question.builder()
                .video(video)
                .build();
    }

    private Reply createReply(Video video) {
        return Reply.builder()
                .video(video)
                .build();
    }
}