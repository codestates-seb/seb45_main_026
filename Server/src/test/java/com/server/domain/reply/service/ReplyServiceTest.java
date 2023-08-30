package com.server.domain.reply.service;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.reply.dto.ReplyDto;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.replyException.ReplyNotFoundException;
import com.server.global.reponse.ApiPageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ReplyServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReplyRepository replyRepository;

    @InjectMocks
    private ReplyService replyService;

    @DisplayName("수강평수정 테스트")
    @Test
    public void updateReply() {
        // Given
        Member member = new Member();
        Long memberId = 1L;

        Long replyId = 1L;
        ReplyDto replyDto = new ReplyDto(1L, memberId, "Test content", 5, member, null);
        replyDto.setContent("Updated content");
        replyDto.setStar(4);

        Reply existingReply = mock(Reply.class);
        when(existingReply.getReplyId()).thenReturn(replyId);
        when(existingReply.getContent()).thenReturn("Initial content");
        when(existingReply.getStar()).thenReturn(3);
        when(replyRepository.findById(eq(replyId))).thenReturn(Optional.of(existingReply));

        Reply updatedReply = mock(Reply.class);
        when(updatedReply.getReplyId()).thenReturn(replyId);
        when(updatedReply.getContent()).thenReturn(replyDto.getContent());
        when(updatedReply.getStar()).thenReturn(replyDto.getStar());
        when(replyRepository.save(any(Reply.class))).thenReturn(updatedReply);

        // When
        Reply result = replyService.updateReply(replyId, replyDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(replyDto.getContent());
        assertThat(result.getStar()).isEqualTo(replyDto.getStar());
    }

    @DisplayName("수강평삭제 테스트")
    @Test
    public void deleteReply() {
        // Given
        Long replyId = 1L;

        Reply replyId1 = mock(Reply.class);
        when(replyRepository.findById(eq(replyId))).thenReturn(Optional.of(replyId1));

        // When
        replyService.deleteReply(replyId);

        // Then
        verify(replyRepository, times(1)).findById(eq(replyId));
        verify(replyRepository, times(1)).delete(replyId1);
    }

    @DisplayName("존재하지않는수강평 테스트")
    @Test
    public void notexistReply() {
        // Given
        Long replyId = 1L;
        when(replyRepository.findById(eq(replyId))).thenReturn(Optional.empty());

        // When, Then
        assertThrows(ReplyNotFoundException.class, () -> replyService.deleteReply(replyId));
    }

    @DisplayName("전체댓글목록 조회")
    @Test
    public void getReplylist() {
        int page = 1;
        int replyListPage = 10;
        Sort sort = Sort.by("createdDate");
        Pageable pageable = PageRequest.of(page, replyListPage, sort);

        Page<Reply> replyPage = new PageImpl<>(Collections.emptyList());
        when(replyRepository.findReplyBy(any(Reply.class), eq(pageable))).thenReturn(replyPage);

        ApiPageResponse<Reply> result = replyService.getReply(page, sort);

        assertNotNull(result);
        assertEquals(replyPage.getContent(), result.getData());
    }

    @DisplayName("댓글생성 테스트")
    @Test
    public void createReply() {
        Member member = new Member();
        Long memberId = 1L;
        Long loginMemberId = 1L;
        ReplyDto replyDto = new ReplyDto(1L, memberId, "Test content", 5, member, null);
        when(memberRepository.findById(eq(replyDto.getMemberId()))).thenReturn(Optional.of(member));

        Reply reply = new Reply();
        when(replyRepository.save(any(Reply.class))).thenReturn(reply);

        Reply createdReply = replyService.createReply(memberId, loginMemberId, replyDto);

        assertNotNull(createdReply);
    }

    @DisplayName("권한없는사용자 테스트")
    @Test
    public void accessDenined() {
        Member member = new Member();
        Long memberId = 1L;
        Long loginMemberId = 2L;
        ReplyDto replyDto = new ReplyDto(1L, memberId, "Test content", 5, member, null);

        assertThrows(MemberAccessDeniedException.class, () -> {
            replyService.createReply(memberId, loginMemberId, replyDto);
        });
    }
}
