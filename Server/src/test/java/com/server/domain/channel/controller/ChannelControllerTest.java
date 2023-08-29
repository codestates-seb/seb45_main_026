package com.server.domain.channel.controller;

import com.server.domain.channel.service.ChannelService;
import com.server.domain.channel.service.dto.ChannelDto;
import com.server.global.reponse.ApiSingleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelControllerTest {

    @Mock
    private ChannelService channelService;

    @InjectMocks
    private ChannelController channelController;

    @DisplayName("채널정보조회테스트")
    @Test
    void testGetChannelInfo() {
        Long memberId = 1L;
        Long channelId = 123L;

        ChannelDto.ChannelInfo channelInfo = new ChannelDto.ChannelInfo();
        channelInfo.setMemberId(memberId);
        channelInfo.setChannelName("GookLuck");
        channelInfo.setSubscribers(9999);
        channelInfo.setCreatedDate(LocalDateTime.of(2023, 8, 29, 12, 58, 58, 123456789));

        when(channelService.getChannelInfo(anyLong())).thenReturn(channelInfo);

        ResponseEntity<ApiSingleResponse<ChannelDto.ChannelInfo>> responseEntity = channelController.getChannelInfo(memberId, channelId);

        assertEquals(200, responseEntity.getStatusCodeValue());

        ApiSingleResponse<ChannelDto.ChannelInfo> responseBody = responseEntity.getBody();
        assertEquals("조회가 완료되었습니다.", responseBody.getMessage());

        ChannelDto.ChannelInfo returnedChannelInfo = responseBody.getData();
        assertEquals(memberId, returnedChannelInfo.getMemberId());
        assertEquals("GookLuck", returnedChannelInfo.getChannelName());
        assertEquals(9999, returnedChannelInfo.getSubscribers());
        assertEquals(LocalDateTime.of(2023, 8, 29, 12, 58, 58, 123456789), returnedChannelInfo.getCreatedDate());
    }
}