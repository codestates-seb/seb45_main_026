package com.server.chat.controller;

import com.server.chat.entity.ChatMessage;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.testhelper.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.server.global.testhelper.RestDocsUtil.pageResponseFields;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserChatControllerTest extends ControllerTest {

    private final String BASE_URL = "/user/chats";

    @Test
    @DisplayName("자신의 채팅방 대화 조회 API")
    void getMessages() throws Exception {
        //given
        int size = 3;
        int page = 1;
        List<ChatMessage> chatMessages = createChatMessages(size);
        Page<ChatMessage> chatMessagePage = createPage(chatMessages, page, size, 3);

        given(chatService.getChatRecord(anyString(), anyString(), anyInt())).willReturn(chatMessagePage);

        String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(chatMessagePage, "채팅 메시지 조회 성공"));

        //when
        ResultActions actions = mockMvc.perform(get(BASE_URL + "/my-rooms")
                .header(AUTHORIZATION, TOKEN)
                .param("page", String.valueOf(page))
                .accept(MediaType.APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse))
        ;

        //restDocs
        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호 (사이즈는 20 고정)")
                        ),
                        pageResponseFields(
                                fieldWithPath("data").description("채팅 메시지"),
                                fieldWithPath("data[].roomId").description("채팅방 ID"),
                                fieldWithPath("data[].sender").description("메시지 보낸 사람"),
                                fieldWithPath("data[].message").description("메시지"),
                                fieldWithPath("data[].sendDate").description("보낸 시간")
                        )
                )
        );
    }

    @Test
    @DisplayName("채팅방 삭제 API")
    void exitChat() throws Exception {
        //given

        //when
        ResultActions actions = mockMvc.perform(delete(BASE_URL)
                .header(AUTHORIZATION, TOKEN))
        ;

        //then
        actions
                .andExpect(status().isNoContent());

        //restDocs
        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        )
                )
        );
    }

    private List<ChatMessage> createChatMessages(int size) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            ChatMessage chatMessage = new ChatMessage(
                    "test@test.com",
                    "test@test.com",
                    "this is message" + i,
                    LocalDateTime.now()
            );
            chatMessages.add(chatMessage);
        }
        return chatMessages;
    }
}