package com.server.domain.reward.repository;

import com.server.domain.member.entity.Member;
import com.server.domain.question.entity.Question;
import com.server.domain.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import com.server.domain.reward.entity.Reward;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RewardRepository extends JpaRepository<Reward, Long> {

    List<Reward> findByMemberAndVideo(Member member, Video video);

    @Query("select r from Reward r " +
            "join fetch r.member m " +
            "join r.video v " +
            "join v.orderVideos ov " +
            "join ov.order o " +
            "where o.orderId = :orderId")
    List<Reward> findByOrderId(String orderId);

    @Query("select r from Reward r " +
            "join r.member m " +
            "join r.question q " +
            "where m = :member and q = :question and r.isCanceled = false")
    Optional<Reward> findByQuestionAndMember(Question question, Member member);

    @Query("select r from Reward r " +
            "join r.member m " +
            "join r.question q " +
            "where m = :member and q in :questions and r.isCanceled = false")
    List<Reward> findByQuestionsAndMember(List<Question> questions, Member member);
}
