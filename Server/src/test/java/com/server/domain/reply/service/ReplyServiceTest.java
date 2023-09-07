package com.server.domain.reply.service;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.reply.controller.convert.ReplySort;
import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.dto.ReplyUpdateServiceApi;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.service.VideoService;
import com.server.global.exception.businessexception.replyException.ReplyNotFoundException;
import com.server.global.testhelper.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ReplyServiceTest extends ServiceTest {

    @Autowired private ReplyService replyService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ReplyRepository replyRepository;
    @Autowired private VideoService videoService;

    @Test
    @DisplayName("댓글을 수정한다.")
    void updateReply() {
        Member loginMember = createAndSaveMember();
        Channel channel = createAndSaveChannel(loginMember);
        Video video = createAndSaveVideo(channel);

        Long loginMemberId = loginMember.getMemberId();
        Long replyId = createAndSaveReply(loginMember, video).getReplyId();

        replyService.updateReply(loginMemberId, replyId, ReplyUpdateServiceApi.builder()
                .content("updateContent")
                .star(5)
                .build());

        assertEquals(replyRepository.findById(replyId).get().getContent(), "updateContent");
    }

    @Test
    @DisplayName("댓글 한 건을 조회한다.")
    void getReply() {
        Member loginMember = createAndSaveMember();
        memberRepository.save(loginMember);

        Channel channel = createAndSaveChannel(loginMember);
        Video video = createAndSaveVideo(channel);

        Long loginMemberId = loginMember.getMemberId();
        Long replyId = createAndSaveReply(loginMember, video).getReplyId();

        ReplyInfo reply = replyService.getReply(replyId, loginMemberId);

        assertEquals(reply.getContent(), "content");
        assertEquals(reply.getStar(), 0);

    }

    @Test
    @DisplayName("댓글을 삭제한다.")
    public void testDeleteReply() {
        Member member = createAndSaveMember();
        Reply reply = createAndSaveReply(member, createAndSaveVideo(createAndSaveChannel(member)));

        Long replyId = reply.getReplyId();
        Long loginMemberId = member.getMemberId();


        replyService.deleteReply(replyId, loginMemberId);
}

    @Test
    @DisplayName("댓글 목록을 페이징으로 찾아서 조회한다.")
    void getReplies() {
        Member member = createAndSaveMember();
        memberRepository.save(member);

        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        for (int i = 0; i < 100; i++) {
            Reply reply = Reply.builder()
                    .content("content" + i)
                    .star(i)
                    .member(member)
                    .video(video)
                    .build();
            replyRepository.save(reply);
        }

        Page<ReplyInfo> replies = videoService.getReplies(video.getVideoId(), 8, 10, ReplySort.CREATED_DATE, null);

        assertEquals(replies.getTotalElements(), 100);
        assertEquals(replies.getNumber(), 8);
        assertEquals(replies.getSize(), 10);
    }

    @Test
    @DisplayName("댓글을 작성한다.")
    void createReply(){
        Member member = createAndSaveMember();
        memberRepository.save(member);

        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        Reply reply = Reply.builder()
                .content("discription")
                .star(0)
                .member(member)
                .video(video)
                .build();

        replyRepository.save(reply);

        assertEquals(replyRepository.findById(reply.getReplyId()).get().getContent(), "discription");
    }

    @Test
    @DisplayName("별점이 8인 댓글을 조회한다")
    void getRepliesWithHighStarFilter() {
        Member member = createAndSaveMember();
        memberRepository.save(member);

        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        List<Reply> replies = new ArrayList<>();

        // 별점이 8점 이상인 댓글들
        for (int i = 8; i <= 10; i++) {
            Reply reply = Reply.builder()
                    .content("content" + i)
                    .star(i)
                    .member(member)
                    .video(video)
                    .build();
            replies.add(replyRepository.save(reply));
        }

        for (int i = 1; i <= 7; i++) {
            Reply reply = Reply.builder()
                    .content("content" + i)
                    .star(i)
                    .member(member)
                    .video(video)
                    .build();
            replies.add(replyRepository.save(reply));
        }

        Page<ReplyInfo> repliesPage = videoService.getReplies(video.getVideoId(), 1, 10, ReplySort.STAR, 8);

        // 별점이 8 이상인 댓글만 조회되었는지 확인하는 코드
        List<ReplyInfo> replyInfoList = repliesPage.getContent();
        for (ReplyInfo replyInfo : replyInfoList) {
            assertTrue(replyInfo.getStar() == 8);
        }

        // 8,9,10 - 댓글 세 개
        assertEquals(repliesPage.getTotalElements(), 3);
        assertEquals(repliesPage.getNumber(), 1);
        assertEquals(repliesPage.getSize(), 10);
    }



    @Test
    @DisplayName("댓글을 최신순으로 조회한다")
    void getRepliesByCreatedDate() {
        Member member = createAndSaveMember();
        memberRepository.save(member);

        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        for (int i = 0; i < 10; i++) {
            Reply reply = Reply.builder()
                    .content("content" + i)
                    .star(i)
                    .member(member)
                    .video(video)
                    .build();
            replyRepository.save(reply);
        }
        replyRepository.findAll();

        Page<ReplyInfo> replies = videoService.getReplies(video.getVideoId(), 1, 10, ReplySort.CREATED_DATE, null);

        assertEquals(replies.getTotalElements(), 10);
        assertEquals(replies.getNumber(), 1);
        assertEquals(replies.getSize(), 10);

        List<ReplyInfo> replyInfoList = replies.getContent();
        for (int i = 0; i < replyInfoList.size() - 1; i++) { //마지막 요소까지
            assertTrue(replyInfoList.get(i).getCreatedDate().compareTo(replyInfoList.get(i + 1).getCreatedDate()) >= 0);
        }
    }

    @Test
    @DisplayName("로그인한 회원만 댓글을 작성할 수 있다.")
    void createReplyLoginUser(){
        Member member = createAndSaveMember();
        memberRepository.save(member);

        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        Reply reply = Reply.builder()
                .content("content")
                .star(0)
                .member(member)
                .video(video)
                .build();

        replyRepository.save(reply);

        assertEquals(replyRepository.findById(reply.getReplyId()).get().getContent(), "content");
    }


}