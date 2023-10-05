package com.server.chat.controller;

import com.server.chat.entity.ChatMessage;
import com.server.chat.entity.ChatRoom;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.server.global.testhelper.RestDocsUtil.pageResponseFields;
import static com.server.global.testhelper.RestDocsUtil.singleResponseFields;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminChatControllerTest extends ControllerTest {

    private final String BASE_URL = "/admin/chats";

    @Test
    @DisplayName("미할당 채팅방 목록 조회")
    void rooms() throws Exception {
        //given
        List<ChatRoom> chatRooms = createChatRooms(3);
        List<String> chatRoomIds = chatRooms.stream()
                .map(ChatRoom::getRoomId)
                .collect(Collectors.toList());

        given(chatService.getChatRooms()).willReturn(chatRooms);

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(chatRoomIds, "미할당 채팅방 목록 조회 성공"));

        //when
        ResultActions actions = mockMvc.perform(get(BASE_URL)
                .header(AUTHORIZATION, TOKEN)
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
                                headerWithName(AUTHORIZATION).description("액세스 토큰 / 관리자용")
                        ),
                        singleResponseFields(
                                fieldWithPath("data").description("미할당 채팅방 목록")
                        )
                )
        );
    }

    @Test
    @DisplayName("자신이 참여한 채팅방 목록 조회 API")
    void myRooms() throws Exception {
        //given
        List<ChatRoom> chatRooms = createChatRooms(3);
        List<String> chatRoomIds = chatRooms.stream()
                .map(ChatRoom::getRoomId)
                .collect(Collectors.toList());

        given(chatService.getMyAdminRooms(anyString())).willReturn(chatRoomIds);

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(chatRoomIds, "자신이 참여한 채팅방 목록 조회 성공"));

        //when
        ResultActions actions = mockMvc.perform(get(BASE_URL + "/my-rooms")
                .header(AUTHORIZATION, TOKEN)
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
                                headerWithName(AUTHORIZATION).description("액세스 토큰 / 관리자용")
                        ),
                        singleResponseFields(
                                fieldWithPath("data").description("자신에게 할당된 채팅방 목록")
                        )
                )
        );
    }

    @Test
    @DisplayName("채팅방 이전 대화 조회 API")
    void getMessages() throws Exception {
        //given
        String roomId = "test@gmail.com";
        int size = 3;
        int page = 1;
        List<ChatMessage> chatMessages = createChatMessages(size);
        Page<ChatMessage> chatMessagePage = createPage(chatMessages, page, size, 3);

        given(chatService.getChatRecord(anyString(), anyString(), anyInt())).willReturn(chatMessagePage);

        String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(chatMessagePage, "채팅 메시지 조회 성공"));

        //when
        ResultActions actions = mockMvc.perform(get(BASE_URL + "/{room-id}", roomId)
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
                                headerWithName(AUTHORIZATION).description("액세스 토큰 / 관리자용")
                        ),
                        pathParameters(
                                parameterWithName("room-id").description("채팅방 아이디")
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
    @DisplayName("상담 완료 처리 API")
    void completeChat() throws Exception {
        //given
        String roomId = "test@gmail.com";

        //when
        ResultActions actions = mockMvc.perform(patch(BASE_URL + "/{room-id}", roomId)
                .header(AUTHORIZATION, TOKEN)
                .accept(MediaType.APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isNoContent())
        ;

        //restDocs
        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰 / 관리자용")
                        ),
                        pathParameters(
                                parameterWithName("room-id").description("채팅방 아이디")
                        )
                )
        );
    }

    private List<ChatRoom> createChatRooms(int size) {
        List<ChatRoom> chatRooms = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            ChatRoom chatRoom = ChatRoom.create("test" + i + "@test.com");
            chatRooms.add(chatRoom);
        }
        return chatRooms;
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