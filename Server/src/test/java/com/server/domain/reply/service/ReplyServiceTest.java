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
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.replyException.ReplyNotFoundException;
import com.server.global.testhelper.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
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

        //given
        Long loginMemberId = createAndSaveMember().getMemberId();
        Long replyId = createAndSaveReply(loginMember, video).getReplyId();

        //when
        replyService.updateReply(loginMemberId, replyId, ReplyUpdateServiceApi.builder()
                .content("updateContent")
                .star(5)
                .build());

        //then
        assertEquals(replyRepository.findById(replyId).get().getContent(), "updateContent");
    }

    @Test
    @DisplayName("댓글을 한 건 조회한다.")
    void getReply() {
        Member loginMember = createAndSaveMember();
        memberRepository.save(loginMember);

        Channel channel = createAndSaveChannel(loginMember);
        Video video = createAndSaveVideo(channel);

        //given
        Long loginMemberId = createAndSaveMember().getMemberId();
        Long replyId = createAndSaveReply(loginMember, video).getReplyId();

        //when
        ReplyInfo reply = replyService.getReply(replyId, loginMemberId);

        //then
        assertEquals(reply.getContent(), "content");
        assertEquals(reply.getStar(), 0);

    }

    @Test
    @DisplayName("댓글을 삭제한다.")
    void deleteReply() {
        // Given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);
        Reply reply = createAndSaveReply(member, video);
        replyRepository.save(reply);

        // When
        replyService.deleteReply(reply.getReplyId(), member.getMemberId());

        // Then
        Reply deletedReply = replyRepository.findById(reply.getReplyId()).orElse(null);
        assertNull(deletedReply);
    }

    @Test
    @DisplayName("댓글 목록을 페이징으로 찾아서 조회한다.")
    void getReplies() {
        // Given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        List<Reply> replies = generateReplies(member, video, 100);

        Long videoId = video.getVideoId();
        int page = 87;
        int size = 10;
        ReplySort replySort = ReplySort.CREATED_DATE;

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, replySort.getSort());

        when(replyRepository.findAllByVideoIdPaging(videoId, pageRequest))
                .thenReturn(new PageImpl<>(replies.stream().map(ReplyInfo::of).collect(Collectors.toList())));

        // When
        Page<ReplyInfo> resultPage = videoService.getReplies(videoId, page, size, replySort, null);

        // Then
        verify(replyRepository).findAllByVideoIdPaging(videoId, pageRequest);
        assertEquals(resultPage.getTotalElements(), 100);
        assertEquals(resultPage.getNumber(), page);
        assertEquals(resultPage.getSize(), size);
    }

    private List<Reply> generateReplies(Member member, Video video, int count) {
        List<Reply> replies = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Reply reply = createAndSaveReply(member, video);
            replies.add(reply);
        }
        return replies;
    }

    @Test
    @DisplayName("댓글을 작성한다.")
    void createReply(){
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

    @Test
    @DisplayName("댓글을 별점순으로 조회한다")
    void getRepliesByStar(){
        Member member = createAndSaveMember();
        memberRepository.save(member);

        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        for(int i = 0; i < 10; i++){
            Reply reply = Reply.builder()
                    .content("content" + i)
                    .star(i)
                    .member(member)
                    .video(video)
                    .build();
            replyRepository.save(reply);
        }
        replyRepository.findAll();

        Page<ReplyInfo> replies = videoService.getReplies(video.getVideoId(), 1, 10,  ReplySort.STAR, null);

        assertEquals(replies.getTotalElements(), 10);
        assertEquals(replies.getNumber(), 1);
        assertEquals(replies.getSize(), 10);
    }

    @Test
    @DisplayName("댓글을 최신순으로 조회한다")
    void getRepliesByCreatedDate(){
        Member member = createAndSaveMember();
        memberRepository.save(member);

        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        for(int i = 0; i < 10; i++){
            Reply reply = Reply.builder()
                    .content("content" + i)
                    .star(i)
                    .member(member)
                    .video(video)
                    .build();
            replyRepository.save(reply);
        }
        replyRepository.findAll();

        Page<ReplyInfo> replies = videoService.getReplies(video.getVideoId(), 1, 10,  ReplySort.CREATED_DATE, null);

        assertEquals(replies.getTotalElements(), 10);
        assertEquals(replies.getNumber(), 1);
        assertEquals(replies.getSize(), 10);
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