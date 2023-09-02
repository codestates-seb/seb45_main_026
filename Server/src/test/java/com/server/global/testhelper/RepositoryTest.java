package com.server.global.testhelper;

import com.server.domain.answer.entity.Answer;
import com.server.domain.answer.entity.AnswerStatus;
import com.server.domain.category.entity.Category;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.question.entity.Question;
import com.server.domain.reward.entity.Reward;
import com.server.domain.reward.entity.RewardType;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.video.entity.Video;
import com.server.domain.video.entity.VideoStatus;
import com.server.domain.videoCategory.entity.VideoCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.List;

@DataJpaTest
public abstract class RepositoryTest {

    @Autowired protected EntityManager em;

    protected Member createAndSaveMember() {
        Member member = Member.builder()
                .email("test@gmail.com")
                .password("1q2w3e4r!")
                .nickname("test")
                .authority(Authority.ROLE_USER)
                .reward(1000)
                .imageFile("imageFile")
                .build();

        em.persist(member);

        return member;
    }

    protected Channel createAndSaveChannel(Member member) {
        Channel channel = Channel.createChannel("channelName");
        channel.setMember(member);
        em.persist(channel);

        return channel;
    }

    protected Video createAndSaveVideo(Channel channel) {
        Video video = Video.builder()
                .videoName("title")
                .description("description")
                .thumbnailFile("thumbnailFile")
                .videoFile("videoFile")
                .channel(channel)
                .view(0)
                .star(0.0F)
                .price(1000)
                .videoStatus(VideoStatus.CREATED)
                .build();

        em.persist(video);

        return video;
    }

    protected Video createAndSaveFreeVideo(Channel channel) {
        Video video = Video.builder()
                .videoName("title")
                .description("description")
                .thumbnailFile("thumbnailFile")
                .videoFile("videoFile")
                .channel(channel)
                .view(0)
                .star(0.0F)
                .price(0)
                .videoStatus(VideoStatus.CREATED)
                .build();

        em.persist(video);

        return video;
    }

    protected Video createAndSaveVideo(Channel channel, int view) {
        Video video = Video.builder()
                .videoName("title")
                .description("description")
                .thumbnailFile("thumbnailFile")
                .videoFile("videoFile")
                .channel(channel)
                .view(view)
                .star(0.0F)
                .price(1000)
                .videoStatus(VideoStatus.CREATED)
                .build();

        em.persist(video);

        return video;
    }

    protected Video createAndSaveVideo(Channel channel, Float star) {
        Video video = Video.builder()
                .videoName("title")
                .description("description")
                .thumbnailFile("thumbnailFile")
                .videoFile("videoFile")
                .channel(channel)
                .view(0)
                .star(star)
                .price(1000)
                .videoStatus(VideoStatus.CREATED)
                .build();

        em.persist(video);

        return video;
    }

    protected Order createAndSaveOrder(Member member, List<Video> video) {

        Order order = Order.createOrder(member, video, 500);

        em.persist(order);

        return order;
    }

    protected Order createAndSaveOrderComplete(Member member, List<Video> video) {

        Order order = Order.createOrder(member, video, 500);
        order.completeOrder();
        em.persist(order);

        return order;
    }

    protected Category createAndSaveCategory(String categoryName) {
        Category category = Category.builder()
                .categoryName(categoryName)
                .build();

        em.persist(category);

        return category;
    }

    protected void createAndSaveVideoCategory(Video video, Category... categorys) {

        for (Category category : categorys) {
            VideoCategory videoCategory = VideoCategory.builder()
                    .video(video)
                    .category(category)
                    .build();

            em.persist(videoCategory);
        }
    }

    protected void createAndSaveSubscribe(Member member, Channel channel) {
        Subscribe subscribe = Subscribe.builder()
                .member(member)
                .channel(channel)
                .build();

        em.persist(subscribe);
    }


    protected Reward createAndSaveVideoReward(Member member, Video video) {

        Reward reward = Reward.createReward(RewardType.VIDEO, 10, member, video);

        em.persist(reward);

        return reward;
    }

    protected Reward createAndSaveQuestionReward(Member member, Question question) {

        Reward reward = Reward.createReward(RewardType.QUIZ, 10, member, question);

        em.persist(reward);

        return reward;
    }

    protected Question createAndSaveQuestion(Video video) {
        Question question = Question.builder()
                .position(1)
                .content("content")
                .questionAnswer("1")
                .selections(List.of("1", "2", "3", "4", "5"))
                .video(video)
                .build();

        em.persist(question);

        return question;
    }

    protected Answer createAndSaveAnswer(Member member, Question question) {
        Answer answer = Answer.builder()
                .member(member)
                .question(question)
                .myAnswer("1")
                .answerStatus(AnswerStatus.WRONG)
                .build();

        em.persist(answer);

        return answer;
    }
}
